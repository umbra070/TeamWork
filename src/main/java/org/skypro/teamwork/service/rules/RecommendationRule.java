package org.skypro.teamwork.service.rules;

import org.skypro.teamwork.dto.Recommendation;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRule {
    Optional<Recommendation> evaluate(UUID userId);
}
