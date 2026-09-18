package org.skypro.teamwork.service;

import org.skypro.teamwork.dto.Recommendation;
import org.skypro.teamwork.dto.RecommendationResponse;
import org.skypro.teamwork.service.rules.RecommendationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {
    @Autowired
    private List<RecommendationRule> rules;

    Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    public RecommendationResponse getRecommendations(UUID userId){
        List<Recommendation> recommendations = rules.stream()
                .map(rule -> rule.evaluate(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        for(Recommendation r : recommendations){
            logger.info("Chosen recommendation: " + r.getName() + " for user: " + userId);
        }
        return new RecommendationResponse(userId, recommendations);
    }
}
