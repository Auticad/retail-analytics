package io.github.auticad.retail.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** unitPrice è facoltativo: se manca, la vendita usa il prezzo di listino del prodotto. */
public record SaleRequest(
        @NotNull(message = "La data è obbligatoria")
        LocalDate saleDate,

        @NotNull(message = "Il negozio è obbligatorio")
        Long storeId,

        @NotNull(message = "Il prodotto è obbligatorio")
        Long productId,

        @NotNull(message = "La quantità è obbligatoria")
        @Positive(message = "La quantità deve essere maggiore di zero")
        Integer quantity,

        @Positive(message = "Il prezzo deve essere maggiore di zero")
        BigDecimal unitPrice) {
}
