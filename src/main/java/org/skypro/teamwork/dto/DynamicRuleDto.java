package org.skypro.teamwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.skypro.teamwork.model.DynamicRuleEntity;
import org.skypro.teamwork.model.RuleQuery;

import java.util.List;
import java.util.UUID;

public class DynamicRuleDto {

    private Long id;

    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_id")
    private UUID productId;
    @JsonProperty("product_text")
    private String productText;

    private List<RuleQuery> rule;

    public DynamicRuleDto() {
    }

    public DynamicRuleDto(String productName, UUID productId, String productText, List<RuleQuery> rule) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<RuleQuery> getRule() {
        return rule;
    }

    public void setRule(List<RuleQuery> rule) {
        this.rule = rule;
    }
}
