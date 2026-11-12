package io.github.auticad.retail.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import io.github.auticad.retail.dto.StoreResponse;
import io.github.auticad.retail.service.NotFoundException;
import io.github.auticad.retail.service.StoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test "a fetta": carica solo lo strato web (controller, validazione, gestione errori, JSON).
 * Il service è sostituito da un mock, quindi non servono né database né Docker.
 */
@WebMvcTest(StoreApiController.class)
class StoreApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    StoreService stores;

    @Test
    void restituisceIlNegozio() throws Exception {
        given(stores.findById(7L)).willReturn(new StoreResponse(7L, "MB01", "Monza Centro", "Monza", "MB",
                1L, "Milano e Brianza", 800, LocalDate.of(2019, 5, 20), true));

        mvc.perform(get("/api/v1/stores/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("MB01"))
                .andExpect(jsonPath("$.openedOn").value("2019-05-20"));
    }

    @Test
    void negozioInesistenteRisponde404InFormatoProblem() throws Exception {
        given(stores.findById(99L)).willThrow(new NotFoundException("Negozio 99 non trovato"));

        mvc.perform(get("/api/v1/stores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Risorsa non trovata"))
                .andExpect(jsonPath("$.detail").value("Negozio 99 non trovato"));
    }

    @Test
    void datiNonValidiRispondono400ConIlDettaglioDeiCampi() throws Exception {
        String body = """
                {"code": "", "name": "Senza codice", "city": "Monza", "province": "MONZA",
                 "districtId": 1, "sqm": -5, "openedOn": "2026-01-10", "active": true}
                """;

        mvc.perform(post("/api/v1/stores").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists())
                .andExpect(jsonPath("$.errors.province").exists())
                .andExpect(jsonPath("$.errors.sqm").exists());

        verify(stores, never()).create(any());
    }

    @Test
    void creazioneRisponde201ConLocation() throws Exception {
        given(stores.create(any())).willReturn(new StoreResponse(13L, "VA01", "Varese Centro", "Varese", "VA",
                1L, "Milano e Brianza", 380, LocalDate.of(2026, 9, 15), true));
        String body = """
                {"code": "VA01", "name": "Varese Centro", "city": "Varese", "province": "VA",
                 "districtId": 1, "sqm": 380, "openedOn": "2026-09-15", "active": true}
                """;

        mvc.perform(post("/api/v1/stores").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/stores/13"))
                .andExpect(jsonPath("$.id").value(13));
    }
}
