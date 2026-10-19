package org.skypro.teamwork.repos;

import org.skypro.teamwork.model.RuleStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 * JPA-репозиторий для работы со статистикой динамических правил.
 */
public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {
    /**
     * Атомарно увеличивает счетчик срабатываний для указанного правила.
     * Использование JPQL-запроса UPDATE гарантирует потокобезопасность.
     *
     * @param ruleId идентификатор правила
     */
    @Modifying
    @Query("UPDATE RuleStatsEntity s SET s.count = s.count + 1 WHERE s.ruleId = :ruleId")
    void increment(@Param("ruleId") Long ruleId);
}
