package io.github.auticad.retail.dto;

import java.math.BigDecimal;

public record CategorySales(String category, BigDecimal revenue, BigDecimal margin,
                            BigDecimal marginPct, long units) {
}
