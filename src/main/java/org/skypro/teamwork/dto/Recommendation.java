package org.skypro.teamwork.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.skypro.teamwork.repos.RecommendationsRepository;

import java.util.UUID;

public class Recommendation {
    private UUID id;
    private String text;
    private String name;

    public Recommendation(UUID id, String name, String text) {
        this.id = id;
        this.text = text;
        this.name = name;
    }

    @Schema(description = "UUID продукта")
    public UUID getId() {
        return id;
    }

    @Schema(description = "Текстовое описание продукта")
    public String getText() {
        return text;
    }

    @Schema(description = "Наименование продукта", example = "Invest 500")
    public String getName() {
        return name;
    }
}
