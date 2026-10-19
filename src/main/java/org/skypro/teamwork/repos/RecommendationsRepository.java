package org.skypro.teamwork.repos;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.skypro.teamwork.repos.cache.UserProductKey;
import org.skypro.teamwork.repos.cache.UserProductTransactionKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
/**
 * Репозиторий для работы с read-only базой данных знаний о пользователях (H2) через JDBC.
 * Содержит кеширование (Caffeine) для снижения нагрузки на БД при частых запросах рекомендаций.
 */
@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Кеш №1: сколько транзакций у пользователя по продуктам указанного типа.
     */
    private final Cache<UserProductKey, Long> transactionCountCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build();

    /**
     * Кеш №2: сумма транзакций указанного типа по продуктам указанного типа.
     */
    private final Cache<UserProductTransactionKey, Long> transactionSumCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build();

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Ищет пользователей по имени и фамилии (без учета регистра).
     * Используется Telegram-ботом. Если найдено несколько пользователей, возвращает список из >1 элемента.
     *
     * @param firstName имя пользователя
     * @param lastName  фамилия пользователя
     * @return список UUID найденных пользователей
     */
    public List<UUID> findUserIdsByFullName(String firstName, String lastName) {
        String sql = """
                SELECT id FROM users
                WHERE LOWER(first_name) = LOWER(?)
                  AND LOWER(last_name)  = LOWER(?)
                """;
        return jdbcTemplate.queryForList(sql, UUID.class, firstName, lastName);
    }

    /**
     * Проверяет, имеет ли пользователь хотя бы одну транзакцию по продукту указанного типа.
     *
     * @param userId идентификатор пользователя
     * @param productType тип продукта (DEBIT, CREDIT, INVEST, SAVING)
     * @return true, если у пользователя есть хотя бы одна транзакция
     */
    public boolean existsUserProductType(UUID userId, String productType) {
        return countTransactionsByProductType(userId, productType) > 0;
    }

    /**
     * Проверяет, что пользователь НЕ использует продукты указанного типа.
     * Является инверсией {@link #existsUserProductType(UUID, String)}.
     *
     * @param userId идентификатор пользователя
     * @param productType тип продукта
     * @return true, если пользователь не имеет транзакций по данному типу
     */
    public boolean doesNotUseProductType(UUID userId, String productType) {
        return !existsUserProductType(userId, productType);
    }

    /**
     * Очищает все локальные кеши (количество транзакций и суммы транзакций).
     * Вызывается при сбросе кеша через Management API.
     */
    public void clearAllCaches() {
        transactionCountCache.invalidateAll();
        transactionSumCache.invalidateAll();
    }

    /**
     * Подсчитывает количество транзакций пользователя по продуктам указанного типа.
     * Результат кешируется (Caffeine) для избежания повторных запросов к БД.
     * Используется в правилах типа ACTIVE_USER_OF.
     *
     * @param userId      идентификатор пользователя
     * @param productType тип продукта (DEBIT, CREDIT, INVEST, SAVING)
     * @return количество транзакций (0, если транзакций нет)
     */
    public long countTransactionsByProductType(UUID userId, String productType) {
        UserProductKey key = new UserProductKey(userId, productType);
        return transactionCountCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ?
                    """;
            Long result = jdbcTemplate.queryForObject(
                    sql, Long.class, k.userId(), k.productType());
            return result != null ? result : 0L;
        });
    }

    /**
     * Вычисляет сумму пополнений (DEPOSIT) по продуктам указанного типа.
     * Является оберткой над {@link #sumTransactionsByType(UUID, String, String)}.
     *
     * @param userId      идентификатор пользователя
     * @param productType тип продукта
     * @return сумма пополнений
     */
    public long sumDepositsByProductType(UUID userId, String productType) {
        return sumTransactionsByType(userId, productType, "DEPOSIT");
    }

    /**
     * Вычисляет сумму трат (WITHDRAW) по продуктам указанного типа.
     * Является оберткой над {@link #sumTransactionsByType(UUID, String, String)}.
     *
     * @param userId      идентификатор пользователя
     * @param productType тип продукта
     * @return сумма трат
     */
    public long sumWithdrawalsByProductType(UUID userId, String productType) {
        return sumTransactionsByType(userId, productType, "WITHDRAW");
    }

    /**
     * Вычисляет сумму транзакций определенного типа по продуктам указанного типа.
     * Результат кешируется (Caffeine). Используется в правилах TRANSACTION_SUM_COMPARE.
     *
     * @param userId          идентификатор пользователя
     * @param productType     тип продукта (DEBIT, CREDIT, INVEST, SAVING)
     * @param transactionType тип транзакции (DEPOSIT, WITHDRAW)
     * @return сумма транзакций (0, если их нет)
     */
    public long sumTransactionsByType(UUID userId, String productType, String transactionType) {
        UserProductTransactionKey key =
                new UserProductTransactionKey(userId, productType, transactionType);
        return transactionSumCache.get(key, k -> {
            String sql = """
                    SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ? AND t.type = ?
                    """;
            Long result = jdbcTemplate.queryForObject(sql, Long.class,
                    k.userId(), k.productType(), k.transactionType());
            return result != null ? result : 0L;
        });
    }
}