package io.github.auticad.retail.dto;

import java.math.BigDecimal;

public record MonthlySales(String month, BigDecimal revenue, BigDecimal margin, long units, BigDecimal target) {
}
