package org.skypro.teamwork.service;

import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.model.DynamicRuleEntity;
import org.skypro.teamwork.repos.DynamicRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {
    @Autowired
    private DynamicRuleRepository drRepository;
    @Autowired
    private RuleStatsService ruleStatsService;

    @Transactional
    public DynamicRuleDto create(DynamicRuleDto dto) {
        DynamicRuleEntity entity = new DynamicRuleEntity();
        entity.setProductName(dto.getProductName());
        entity.setProductText(dto.getProductText());
        entity.setProductId(dto.getProductId());
        entity.setRule(dto.getRule());
        DynamicRuleEntity saved = drRepository.save(entity);
        ruleStatsService.initForRule(saved);
        return toDto(saved);
    }

    @Transactional
    public void deleteByProductId(UUID productId) {
        drRepository.deleteByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<DynamicRuleEntity> getAll() {
        return drRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DynamicRuleDto> getAllAsDto() {
        return getAll().stream().map(this::toDto).toList();
    }

    private DynamicRuleDto toDto(DynamicRuleEntity entity) {
        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductText(entity.getProductText());
        dto.setRule(entity.getRule());
        return dto;
    }
}
