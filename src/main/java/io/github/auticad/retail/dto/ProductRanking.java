package io.github.auticad.retail.dto;

import java.math.BigDecimal;

public record ProductRanking(String sku, String productName, String category, long units, BigDecimal revenue) {
}
