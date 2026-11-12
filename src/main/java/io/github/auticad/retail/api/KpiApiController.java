package io.github.auticad.retail.api;

import java.time.LocalDate;
import java.util.List;

import io.github.auticad.retail.dto.CategorySales;
import io.github.auticad.retail.dto.DistrictSales;
import io.github.auticad.retail.dto.KpiSummary;
import io.github.auticad.retail.dto.MonthlySales;
import io.github.auticad.retail.dto.ProductRanking;
import io.github.auticad.retail.dto.StorePerformance;
import io.github.auticad.retail.service.KpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPI aggregati. Tutti gli endpoint accettano from e to (yyyy-MM-dd); senza parametri
 * il periodo sono gli ultimi 12 mesi con dati. Sono la sorgente "Web" di Power BI.
 */
@RestController
@RequestMapping("/api/v1/kpi")
@Tag(name = "KPI", description = "Indicatori aggregati per dashboard e Power BI")
public class KpiApiController {

    private final KpiService kpi;

    public KpiApiController(KpiService kpi) {
        this.kpi = kpi;
    }

    @GetMapping("/summary")
    @Operation(summary = "Fatturato, margine, pezzi e valore medio riga del periodo")
    public KpiSummary summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return kpi.summary(kpi.resolveRange(from, to));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Andamento mensile con obiettivo")
    public List<MonthlySales> monthly(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return kpi.monthly(kpi.resolveRange(from, to));
    }

    @GetMapping("/stores")
    @Operation(summary = "Classifica dei negozi: fatturato, margine, obiettivo, resa al metro quadro")
    public List<StorePerformance> stores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return kpi.storePerformance(kpi.resolveRange(from, to));
    }

    @GetMapping("/districts")
    @Operation(summary = "Vendite per distretto")
    public List<DistrictSales> districts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return kpi.byDistrict(kpi.resolveRange(from, to));
    }

    @GetMapping("/categories")
    @Operation(summary = "Vendite per categoria merceologica")
    public List<CategorySales> categories(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return kpi.byCategory(kpi.resolveRange(from, to));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Prodotti per fatturato")
    public List<ProductRanking> topProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {
        return kpi.topProducts(kpi.resolveRange(from, to), limit);
    }
}
