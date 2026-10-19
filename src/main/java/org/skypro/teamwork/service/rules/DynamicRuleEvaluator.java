package org.skypro.teamwork.service.rules;

import org.skypro.teamwork.dto.Recommendation;
import org.skypro.teamwork.model.DynamicRuleEntity;
import org.skypro.teamwork.model.RuleQuery;
import org.skypro.teamwork.repos.RecommendationsRepository;
import org.skypro.teamwork.service.RuleStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
/**
 * Компонент, интерпретирующий динамические правила.
 * Преобразует набор абстрактных запросов {@link RuleQuery} в SQL-вызовы через репозиторий
 * и вычисляет итоговый логический результат для правила.
 */
@Component
public class DynamicRuleEvaluator {
    @Autowired
    private RecommendationsRepository rRepository;
    @Autowired
    private RuleStatsService ruleStatsService;

    /**
     * Проверяет, подходит ли пользователь под все условия динамического правила.
     * Если правило выполняется, увеличивает счетчик его срабатываний и возвращает рекомендацию.
     *
     * @param rule сущность динамического правила
     * @param userId уникальный идентификатор пользователя
     * @return {@link Optional} с рекомендацией, если правило выполнено, или {@link Optional#empty()}
     */
    public Optional<Recommendation> evaluate(DynamicRuleEntity rule, UUID userId) {
        for (RuleQuery query : rule.getRule()) {
            boolean result = evaluateQuery(query, userId);
            if (query.isNegate()) {
                result = !result;
            }
            if (!result) {
                return Optional.empty();
            }
        }
        if (rule.getId() != null) {
            ruleStatsService.increment(rule.getId());
        }
        return Optional.of(new Recommendation(
                rule.getProductId(),
                rule.getProductName(),
                rule.getProductText()
        ));

    }

    private boolean evaluateQuery(RuleQuery query, UUID userId) {
        String type = query.getQuery();
        var args = query.getArguments();

        return switch (type) {
            case "USER_OF" -> rRepository.existsUserProductType(userId, args.get(0));

            case "ACTIVE_USER_OF" -> rRepository.countTransactionsByProductType(userId, args.get(0)) >= 5;

            case "TRANSACTION_SUM_COMPARE" -> {
                String productType = args.get(0);
                String transactionType = args.get(1);
                String operator = args.get(2);
                long constant = Long.parseLong(args.get(3));
                long sum = rRepository.sumTransactionsByType(userId, productType, transactionType);
                yield compare(sum, operator, constant);
            }

            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                String productType = args.get(0);
                String operator = args.get(1);
                long deposits = rRepository.sumDepositsByProductType(userId, productType);
                long withdrawals = rRepository.sumWithdrawalsByProductType(userId, productType);
                yield compare(deposits, operator, withdrawals);
            }

            default -> false;
        };
    }

    private boolean compare(long left, String operator, long right) {
        return switch (operator) {
            case ">" -> left > right;
            case "<" -> left < right;
            case "=" -> left == right;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> false;
        };
    }
}
