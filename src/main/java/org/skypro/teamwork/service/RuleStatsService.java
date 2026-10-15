package org.skypro.teamwork.service;

import org.skypro.teamwork.model.DynamicRuleEntity;
import org.skypro.teamwork.model.RuleStatsEntity;
import org.skypro.teamwork.repos.RuleStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RuleStatsService {
    private final RuleStatsRepository repository;

    public RuleStatsService(RuleStatsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void increment(Long ruleId) {
        repository.increment(ruleId);
    }

    @Transactional
    public void initForRule(DynamicRuleEntity rule) {
        RuleStatsEntity stats = new RuleStatsEntity();
        stats.setRule(rule);
        stats.setCount(0L);
        repository.save(stats);
    }

    @Transactional(readOnly = true)
    public List<RuleStatsEntity> getAll() {
        return repository.findAll();
    }
}
