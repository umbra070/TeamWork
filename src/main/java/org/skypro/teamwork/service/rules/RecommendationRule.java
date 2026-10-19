package org.skypro.teamwork.service.rules;

import org.skypro.teamwork.dto.Recommendation;

import java.util.Optional;
import java.util.UUID;
/**
 * Интерфейс для статических (фиксированных) правил рекомендаций.
 */
public interface RecommendationRule {
    /**
     * Проверяет, подходит ли пользователь под условие правила.
     *
     * @param userId идентификатор пользователя
     * @return {@link Optional} с рекомендацией, если правило выполнено, иначе пустой {@link Optional}
     */
    Optional<Recommendation> evaluate(UUID userId);
}
