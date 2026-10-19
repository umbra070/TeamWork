package org.skypro.teamwork.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.service.DynamicRuleService;
import org.skypro.teamwork.service.RuleStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 */
@RestController
@RequestMapping("/rule")
@Tag(name = "Rules", description = "API для управления динамическими правилами рекомендаций")
public class RuleController {
    @Autowired
    private DynamicRuleService drService;
    @Autowired
    private RuleStatsService ruleStatsService;

    /**
     * Создает новое динамическое правило рекомендаций и инициализирует для него счетчик статистики.
     *
     * @param dto DTO с данными нового правила
     * @return {@link ResponseEntity} с сохраненным правилом (включая сгенерированный ID)
     */
    @PostMapping
    @Operation(summary = "Создать новое динамическое правило")
    public ResponseEntity<DynamicRuleDto> create(@RequestBody DynamicRuleDto dto) {
        return ResponseEntity.ok(drService.create(dto));
    }

    /**
     * Возвращает список всех динамических правил, сохраненных в базе данных.
     *
     * @return {@link ResponseEntity} с картой, где ключ "data" содержит список правил
     */
    @GetMapping
    @Operation(summary = "Получить все динамические правила")
    public ResponseEntity<Map<String, List<DynamicRuleDto>>> getAll() {
        return ResponseEntity.ok(Map.of("data", drService.getAllAsDto()));
    }

    /**
     * Возвращает статистику срабатываний по всем динамическим правилам.
     * Если правило ни разу не срабатывало, оно все равно возвращается со значением 0.
     *
     * @return {@link ResponseEntity} с картой, где ключ "stats" содержит список статистик
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> stats() {
        List<Map<String, Object>> stats = ruleStatsService.getAll().stream()
                .map(s -> Map.<String, Object>of(
                        "rule_id", s.getRuleId(),
                        "count", s.getCount()))
                .toList();
        return ResponseEntity.ok(Map.of("stats", stats));
    }

    /**
     * Удаляет динамическое правило по его идентификатору продукта.
     *
     * @param productId UUID продукта, правило для которого нужно удалить
     * @return {@link ResponseEntity} с кодом 204 No Content
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить правило по productId")
    public ResponseEntity<Void> delete(@PathVariable UUID productId) {
        drService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
