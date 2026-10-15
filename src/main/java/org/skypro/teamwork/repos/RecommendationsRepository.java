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

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    /** Кеш №1: сколько транзакций у пользователя по продуктам указанного типа. */
    private final Cache<UserProductKey, Long> transactionCountCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build();

    /** Кеш №2: сумма транзакций указанного типа по продуктам указанного типа. */
    private final Cache<UserProductTransactionKey, Long> transactionSumCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build();

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UUID> findUserIdsByFullName(String firstName, String lastName) {
        String sql = """
            SELECT id FROM users
            WHERE LOWER(first_name) = LOWER(?)
              AND LOWER(last_name)  = LOWER(?)
            """;
        return jdbcTemplate.queryForList(sql, UUID.class, firstName, lastName);
    }


    public boolean existsUserProductType(UUID userId, String productType) {
        return countTransactionsByProductType(userId, productType) > 0;
    }

    public boolean doesNotUseProductType(UUID userId, String productType) {
        return !existsUserProductType(userId, productType);
    }

    public void clearAllCaches() {
        transactionCountCache.invalidateAll();
        transactionSumCache.invalidateAll();
    }

    /** ACTIVE_USER_OF: 5+ транзакций по продукту указанного типа. */
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

    /** Сумма DEPOSIT по продуктам указанного типа (обёртка над sumTransactionsByType). */
    public long sumDepositsByProductType(UUID userId, String productType) {
        return sumTransactionsByType(userId, productType, "DEPOSIT");
    }

    /** Сумма WITHDRAW по продуктам указанного типа (обёртка над sumTransactionsByType). */
    public long sumWithdrawalsByProductType(UUID userId, String productType) {
        return sumTransactionsByType(userId, productType, "WITHDRAW");
    }

    /** TRANSACTION_SUM_COMPARE: сумма транзакций типа Y по продуктам типа X. */
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