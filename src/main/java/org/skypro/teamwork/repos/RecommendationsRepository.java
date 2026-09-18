package org.skypro.teamwork.repos;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Есть ли у пользователя хотя бы одна транзакция по продукту указанного типа. */
    public boolean existsUserProductType(UUID userId, String productType){
        String sql = """
                SELECT COUNT(*) > 0 FROM transactions t JOIN Products p ON t.product_id = p.id 
                WHERE t.user_id = ? AND p.type = ?
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType));
    }

    public boolean doesNotUseProductType(UUID userId, String productType){
        return !existsUserProductType(userId, productType);
    }

    public long sumDepositsByProductType(UUID userId, String productType){
        String sql = """
                SELECT COALESCE(SUM(t.amount), 0) FROM transactions t JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ? AND t.type = 'DEPOSIT'
                """;
        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
        return result != null ? result : 0L;
    }

    public long sumWithdrawalsByProductType(UUID userId, String productType){
        String sql = """
                SELECT COALESCE(SUM(t.amount), 0) FROM transactions t JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ? AND t.type = 'WITHDRAW'
                """;
        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
        return result != null ? result : 0L;
    }
}
