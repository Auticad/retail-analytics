package io.github.auticad.retail.dto;

import java.time.LocalDate;

/** Periodo di analisi, estremi inclusi. */
public record DateRange(LocalDate from, LocalDate to) {

    public DateRange {
        if (from.isAfter(to)) {
            LocalDate swap = from;
            from = to;
            to = swap;
        }
    }
}
