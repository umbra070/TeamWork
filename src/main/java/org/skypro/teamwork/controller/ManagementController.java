package org.skypro.teamwork.controller;

import org.skypro.teamwork.repos.RecommendationsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/**
 * REST-контроллер для технологических задач: сбора информации о сервисе и сброса кеша.
 */
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

    /**
     * Возвращает информацию о сервисе: его название и версию из файла pom.xml.
     * Используется для отображения в общем списке сервисов банка.
     *
     * @return {@link ResponseEntity} с картой, содержащей поля "name" и "version"
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
                "name", applicationName,
                "version", buildProperties.getVersion()
        ));
    }

    /**
     * Сбрасывает все закешированные результаты запросов к базе знаний о пользователях.
     * Используется, когда внешняя система обновляет данные в базе.
     *
     * @return {@link ResponseEntity} с кодом 200 OK
     */
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationsRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }
}
