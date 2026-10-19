package org.skypro.teamwork.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;
/**
 * Сущность динамического правила рекомендации.
 * Поле {@code rule} хранится в базе данных в формате JSONB.
 */
@Table(name = "dynamic_rule")
@Entity
public class DynamicRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(name = "product_text", nullable = false)
    private String productText;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(name = "rule", nullable = false)
    private List<RuleQuery> rule;

    public DynamicRuleEntity() {
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
