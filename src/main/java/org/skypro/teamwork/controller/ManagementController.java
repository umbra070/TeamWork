package org.skypro.teamwork.controller;

import org.skypro.teamwork.repos.RecommendationsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final BuildProperties buildProperties;
    private final String applicationName;
    private final RecommendationsRepository recommendationsRepository;

    public ManagementController(
            RecommendationsRepository recommendationsRepository,
            BuildProperties buildProperties,
            @Value("${spring.application.name:teamwork}") String applicationName) {
        this.buildProperties = buildProperties;
        this.applicationName = applicationName;
        this.recommendationsRepository = recommendationsRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
                "name", applicationName,
                "version", buildProperties.getVersion()
        ));
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationsRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }
}
