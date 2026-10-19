package org.skypro.teamwork.service;

import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.dto.Recommendation;
import org.skypro.teamwork.dto.RecommendationResponse;
import org.skypro.teamwork.model.DynamicRuleEntity;
import org.skypro.teamwork.repos.DynamicRuleRepository;
import org.skypro.teamwork.service.rules.DynamicRuleEvaluator;
import org.skypro.teamwork.service.rules.RecommendationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Основной сервис рекомендательной системы.
 * Объединяет результаты проверки статических правил (зашитых в код) и динамических правил из БД.
 */
@Service
public class RecommendationService {
    @Autowired
    private List<RecommendationRule> rules;

    private final DynamicRuleRepository drRepository;
    private final DynamicRuleService dynamicRuleService;
    private final DynamicRuleEvaluator dynamicRuleEvaluator;

    Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    public RecommendationService(List<RecommendationRule> rules,
                                 DynamicRuleService dynamicRuleService,
                                 DynamicRuleEvaluator dynamicRuleEvaluator,
                                 DynamicRuleRepository drRepository) {
        this.rules = rules;
        this.dynamicRuleService = dynamicRuleService;
        this.dynamicRuleEvaluator = dynamicRuleEvaluator;
        this.drRepository = drRepository;
    }

    @Transactional
    public DynamicRuleEntity create(DynamicRuleDto dto) {
        DynamicRuleEntity entity = new DynamicRuleEntity();
        entity.setProductName(dto.getProductName());
        entity.setProductId(dto.getProductId());
        entity.setProductText(dto.getProductText());
        entity.setRule(dto.getRule());
        return drRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<DynamicRuleEntity> getAll() {
        return drRepository.findAll();
    }

    @Transactional
    public void deleteByProductId(UUID productId) {
        drRepository.deleteByProductId(productId);
    }

    /**
     * Формирует итоговый список рекомендаций для пользователя.
     * Сначала проверяются статические правила, затем динамические правила из базы данных.
     *
     * @param userId уникальный идентификатор пользователя
     * @return объект {@link RecommendationResponse} с итоговым списком продуктов
     */
    public RecommendationResponse getRecommendations(UUID userId) {
        List<Recommendation> recommendations = new ArrayList<>();

        // Фиксированные правила — один проход
        rules.stream()
                .map(rule -> rule.evaluate(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(recommendations::add);

        // Динамические правила — getAll() уже возвращает entity, toEntity не нужен
        dynamicRuleService.getAll().stream()
                .map(entity -> dynamicRuleEvaluator.evaluate(entity, userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(recommendations::add);

        for (Recommendation r : recommendations) {
            logger.info("Chosen recommendation: {} for user: {}", r.getName(), userId);
        }
        return new RecommendationResponse(userId, recommendations);
    }

    private org.skypro.teamwork.model.DynamicRuleEntity toEntity(
            org.skypro.teamwork.dto.DynamicRuleDto dto) {
        var entity = new org.skypro.teamwork.model.DynamicRuleEntity();
        entity.setId(dto.getId());
        entity.setProductName(dto.getProductName());
        entity.setProductId(dto.getProductId());
        entity.setProductText(dto.getProductText());
        entity.setRule(dto.getRule());
        return entity;
    }
}
