package io.github.auticad.retail.web;

import java.math.BigDecimal;

/** Una barra del grafico mensile: widthPct è la lunghezza relativa al mese migliore (0-100). */
public record ChartBar(String label, BigDecimal value, BigDecimal target, int widthPct, boolean targetReached) {
}
