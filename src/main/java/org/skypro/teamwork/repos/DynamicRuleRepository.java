package org.skypro.teamwork.repos;

import org.skypro.teamwork.model.DynamicRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
//
public interface DynamicRuleRepository extends JpaRepository<DynamicRuleEntity, Long> {
    Optional<DynamicRuleEntity> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);
    boolean existsByProductId(UUID productId);
}
