package org.skypro.teamwork.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.repos.DynamicRuleRepository;
import org.skypro.teamwork.service.DynamicRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
@Tag(name="Rules", description = "API для управления динамическими правилами рекомендаций")
public class RuleController {
    @Autowired
    private DynamicRuleService drService;

    @PostMapping
    @Operation(summary = "Создать новое динамическое правило")
    public ResponseEntity<DynamicRuleDto> create(@RequestBody DynamicRuleDto dto){
        return ResponseEntity.ok(drService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Получить все динамические правила")
    public ResponseEntity<Map<String, List<DynamicRuleDto>>> getAll(){
        return ResponseEntity.ok(Map.of("data", drService.getAllAsDto()));
    }
    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить правило по productId")
    public ResponseEntity<Void> delete(@PathVariable UUID productId){
        drService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
