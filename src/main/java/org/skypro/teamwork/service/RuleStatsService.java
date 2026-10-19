package org.skypro.teamwork.service;

import org.skypro.teamwork.model.DynamicRuleEntity;
import org.skypro.teamwork.model.RuleStatsEntity;
import org.skypro.teamwork.repos.RuleStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Сервис для управления статистикой срабатываний динамических правил.
 */
@Service
public class RuleStatsService {
    private final RuleStatsRepository repository;

    public RuleStatsService(RuleStatsRepository repository) {
        this.repository = repository;
    }

    /**
     * Атомарно увеличивает счетчик срабатываний правила на единицу.
     * Вызывается из {@link org.skypro.teamwork.service.rules.DynamicRuleEvaluator} при успешной проверке.
     *
     * @param ruleId идентификатор правила
     */
    @Transactional
    public void increment(Long ruleId) {
        repository.increment(ruleId);
    }

    /**
     * Инициализирует нулевую статистику для нового правила.
     * Вызывается при создании правила, чтобы в таблице статистики появилась запись.
     *
     * @param rule сохраненное правило
     */
    @Transactional
    public void initForRule(DynamicRuleEntity rule) {
        RuleStatsEntity stats = new RuleStatsEntity();
        stats.setRule(rule);
        stats.setCount(0L);
        repository.save(stats);
    }

    /**
     * Возвращает статистику по всем правилам.
     *
     * @return список сущностей статистики
     */
    @Transactional(readOnly = true)
    public List<RuleStatsEntity> getAll() {
        return repository.findAll();
    }
}
