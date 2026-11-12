package io.github.auticad.retail.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record KpiSummary(LocalDate from, LocalDate to, BigDecimal revenue, BigDecimal margin,
                         BigDecimal marginPct, long units, long saleRows, BigDecimal averageRowValue) {
}
