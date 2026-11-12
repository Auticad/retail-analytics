package io.github.auticad.retail.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Dati in ingresso per creare o modificare un negozio. Le regole di validazione stanno qui, non nel controller. */
public record StoreRequest(
        @NotBlank(message = "Il codice è obbligatorio")
        @Size(max = 10, message = "Il codice ha al massimo 10 caratteri")
        String code,

        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 80, message = "Il nome ha al massimo 80 caratteri")
        String name,

        @NotBlank(message = "La città è obbligatoria")
        @Size(max = 60, message = "La città ha al massimo 60 caratteri")
        String city,

        @NotBlank(message = "La provincia è obbligatoria")
        @Size(min = 2, max = 2, message = "La provincia è una sigla di 2 lettere")
        String province,

        @NotNull(message = "Il distretto è obbligatorio")
        Long districtId,

        @NotNull(message = "La superficie è obbligatoria")
        @Positive(message = "La superficie deve essere maggiore di zero")
        Integer sqm,

        @NotNull(message = "La data di apertura è obbligatoria")
        LocalDate openedOn,

        boolean active) {
}
