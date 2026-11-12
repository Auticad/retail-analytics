package io.github.auticad.retail;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test di integrazione: applicazione completa + MySQL reale in Testcontainers.
 * Flyway applica le stesse migrazioni della produzione, quindi i dati di esempio sono disponibili.
 * Il suffisso IT fa eseguire la classe da Failsafe nella fase "verify" (serve Docker avviato).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class RetailAnalyticsApplicationIT {

    @Autowired
    MockMvc mvc;

    @Test
    void leMigrazioniCaricanoLeAnagrafiche() throws Exception {
        mvc.perform(get("/api/v1/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(12)))
                .andExpect(jsonPath("$[0].code").value("BG01"))
                .andExpect(jsonPath("$[0].districtName").value("Lombardia Est"));

        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(24)));
    }

    @Test
    void iKpiDelPeriodoSonoCalcolati() throws Exception {
        mvc.perform(get("/api/v1/kpi/summary").param("from", "2026-01-01").param("to", "2026-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("2026-01-01"))
                .andExpect(jsonPath("$.revenue").isNumber())
                .andExpect(jsonPath("$.saleRows").isNumber());

        mvc.perform(get("/api/v1/kpi/monthly").param("from", "2026-01-01").param("to", "2026-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].month").value("2026-01"));

        mvc.perform(get("/api/v1/kpi/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(12)));
    }

    @Test
    void creaLeggeEdEliminaUnNegozio() throws Exception {
        String body = """
                {"code": "va01", "name": "Varese Centro", "city": "Varese", "province": "va",
                 "districtId": 1, "sqm": 380, "openedOn": "2026-09-15", "active": true}
                """;

        String location = mvc.perform(post("/api/v1/stores").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.code").value("VA01"))
                .andExpect(jsonPath("$.province").value("VA"))
                .andReturn().getResponse().getHeader("Location");

        // stesso codice una seconda volta: 409 in formato problem+json
        mvc.perform(post("/api/v1/stores").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.field").value("code"));

        mvc.perform(get(location)).andExpect(status().isOk());
        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void unNegozioConVenditeNonSiElimina() throws Exception {
        mvc.perform(delete("/api/v1/stores/1")).andExpect(status().isConflict());
    }

    @Test
    void registraUnaVenditaAlPrezzoDiListino() throws Exception {
        String body = """
                {"saleDate": "2026-10-15", "storeId": 1, "productId": 1, "quantity": 2}
                """;

        mvc.perform(post("/api/v1/sales").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.storeCode").value("MI01"))
                .andExpect(jsonPath("$.unitPrice").value(129.90))
                .andExpect(jsonPath("$.amount").value(259.80));
    }

    @Test
    void laDashboardVieneRenderizzata() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("Dashboard vendite")))
                .andExpect(content().string(containsString("Milano Duomo")));

        mvc.perform(get("/stores")).andExpect(status().isOk()).andExpect(view().name("stores/list"));
        mvc.perform(get("/stores/new")).andExpect(status().isOk()).andExpect(view().name("stores/form"));
        mvc.perform(get("/sales")).andExpect(status().isOk()).andExpect(view().name("sales/list"));
        mvc.perform(get("/sales/new")).andExpect(status().isOk()).andExpect(view().name("sales/form"));
    }

    @Test
    void laDocumentazioneOpenApiEDisponibile() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Retail Analytics API"));
    }
}
