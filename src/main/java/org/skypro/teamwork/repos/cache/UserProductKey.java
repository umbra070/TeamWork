package org.skypro.teamwork.repos.cache;

import java.util.UUID;

public record UserProductKey(UUID userId, String productType) {
}