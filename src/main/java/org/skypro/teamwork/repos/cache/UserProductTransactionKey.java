package org.skypro.teamwork.repos.cache;

import java.util.UUID;

public record UserProductTransactionKey(UUID userId, String productType, String transactionType) {
}