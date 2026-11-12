package io.github.auticad.retail.dto;

import java.math.BigDecimal;

public record DistrictSales(String district, String manager, int stores, BigDecimal revenue,
                            BigDecimal margin, BigDecimal marginPct) {
}
