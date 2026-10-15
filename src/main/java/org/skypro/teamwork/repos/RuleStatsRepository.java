package org.skypro.teamwork.repos;

import org.skypro.teamwork.model.RuleStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {
    @Modifying
    @Query("UPDATE RuleStatsEntity s SET s.count = s.count + 1 WHERE s.ruleId = :ruleId")
    void increment(@Param("ruleId") Long ruleId);
}
