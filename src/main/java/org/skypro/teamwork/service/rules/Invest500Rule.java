package org.skypro.teamwork.service.rules;

import org.skypro.teamwork.dto.Recommendation;
import org.skypro.teamwork.repos.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500Rule implements RecommendationRule{
    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_TEXT = "" +
            "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
            "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. " +
            "Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
            "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными " +
            "рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости";


    @Autowired
    private RecommendationsRepository rRepository;
    Logger logger = LoggerFactory.getLogger(Invest500Rule.class);

    @Override
    public Optional<Recommendation> evaluate(UUID userId){
        logger.info("User: " + userId);
        logger.info("Recommendation: Invest500");
        if(!rRepository.existsUserProductType(userId, "DEBIT")){
            logger.info("Status: None; Reason: No product DEBIT");
            return Optional.empty();
        }
        if(!rRepository.doesNotUseProductType(userId, "INVEST")){
            logger.info("Status: None; Reason: Have product INVEST");
            return Optional.empty();
        }
        long savingDeposits = rRepository.sumDepositsByProductType(userId, "SAVING");
        if(savingDeposits <= 1000){
            logger.info("Status: None; Reason: Product SAVING less then 1000");
            return Optional.empty();
        }
        logger.info("Status: YES");
        return Optional.of(new Recommendation(PRODUCT_ID, PRODUCT_NAME, PRODUCT_TEXT));
    }
}
