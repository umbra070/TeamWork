package org.skypro.teamwork.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.teamwork.dto.RecommendationResponse;
import org.skypro.teamwork.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
@Tag(name = "Recommendations", description = "API Для получения персональных рекомендаций банковских продуктов")
public class RecommendationController {
    @Autowired
    private RecommendationService rService;

    @GetMapping("/{userId}")
    @Operation(summary = "Получить рекомендации для пользователя",
    description = "Возвращает список банковских продуктов, рекомендуемых пользователю на основе анализа его транзакций")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable String userId){
        UUID uId = UUID.fromString(userId);
        return ResponseEntity.ok(rService.getRecommendations(uId));
    }
}
