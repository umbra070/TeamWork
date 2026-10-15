package org.skypro.teamwork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rule_stats")
public class RuleStatsEntity {

    @Id
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "count", nullable = false)
    private Long count = 0L;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "rule_id")
    private DynamicRuleEntity rule;

    public RuleStatsEntity() {}

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public DynamicRuleEntity getRule() {
        return rule;
    }

    public void setRule(DynamicRuleEntity rule) {
        this.rule = rule;
    }
}
