package org.skypro.teamwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class RecommendationResponse {
    @JsonProperty("user_id")
    private UUID userId;
    private List<Recommendation> recommendations;

    public RecommendationResponse(UUID userId, List<Recommendation> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
}
