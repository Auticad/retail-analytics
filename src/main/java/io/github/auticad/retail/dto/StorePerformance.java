package io.github.auticad.retail.dto;

import java.math.BigDecimal;

public record StorePerformance(Long storeId, String storeCode, String storeName, String district,
                               BigDecimal revenue, BigDecimal margin, BigDecimal marginPct,
                               BigDecimal target, BigDecimal targetAttainmentPct, BigDecimal revenuePerSqm) {
}
