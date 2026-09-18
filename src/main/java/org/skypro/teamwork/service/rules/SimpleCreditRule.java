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
public class SimpleCreditRule implements RecommendationRule{

    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_TEXT = """
            Откройте мир выгодных кредитов с нами!
            Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то,
            что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
            """;

    @Autowired
    private RecommendationsRepository rRepository;
    Logger logger = LoggerFactory.getLogger(SimpleCreditRule.class);

    @Override
    public Optional<Recommendation> evaluate(UUID userId) {
        logger.info("User: " + userId);
        logger.info("Recommendation: Simple Credit");
        if(!rRepository.doesNotUseProductType(userId, "CREDIT")){
            logger.info("Status: None; Reason: Have product CREDIT");
            return Optional.empty();
        }

        long debitDeposits = rRepository.sumDepositsByProductType(userId, "DEBIT");
        long debitWithdrawals = rRepository.sumWithdrawalsByProductType(userId, "DEBIT");

        if(debitDeposits <= debitWithdrawals){
            logger.info("Status: None; Reason: Deposits less then Withdrawals");
            return Optional.empty();
        }

        if(debitWithdrawals <= 100000){
            logger.info("Status: None; Reason: Withdrawals less than 100 000");
            return Optional.empty();
        }

        logger.info("Status: YES");
        return Optional.of(new Recommendation(PRODUCT_ID, PRODUCT_NAME, PRODUCT_TEXT));
    }
}
