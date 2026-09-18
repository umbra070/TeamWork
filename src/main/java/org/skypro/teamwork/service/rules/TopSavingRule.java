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
public class TopSavingRule implements RecommendationRule{
    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_TEXT = """
    Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент,
                который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и
                потерянных квитанций — всё под контролем!
               """;

    @Autowired
    private RecommendationsRepository rRepository;

    private Logger logger = LoggerFactory.getLogger(TopSavingRule.class);

    @Override
    public Optional<Recommendation> evaluate(UUID userId) {
        logger.info("User: " + userId);
        logger.info("Recommendation: Top Saving");
        long debitDeposits = rRepository.sumDepositsByProductType(userId, "DEBIT");
        long savingDeposit = rRepository.sumDepositsByProductType(userId, "SAVING");
        long debitWithdrawals = rRepository.sumWithdrawalsByProductType(userId, "DEBIT");

        if(!rRepository.existsUserProductType(userId, "DEBIT")){
            logger.info("Status: None; Reason: Has no product DEBIT");
            return Optional.empty();
        }

        if(debitDeposits < 50000 && savingDeposit < 50000){
            logger.info("Status: None; Reason: Debit deposits less than 50 000 and Saving Deposits less than 50 000");
            return Optional.empty();
        }

        if(debitDeposits <= debitWithdrawals){
            logger.info("Status: None; Reason: Debit Deposits less than Withdrawals");
            return Optional.empty();
        }

        logger.info("Status: YES");
        return Optional.of(new Recommendation(PRODUCT_ID, PRODUCT_NAME, PRODUCT_TEXT));
    }
}
