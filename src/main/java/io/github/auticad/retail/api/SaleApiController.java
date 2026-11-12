package io.github.auticad.retail.api;

import java.time.LocalDate;

import io.github.auticad.retail.dto.PageResponse;
import io.github.auticad.retail.dto.SaleRequest;
import io.github.auticad.retail.dto.SaleResponse;
import io.github.auticad.retail.service.KpiService;
import io.github.auticad.retail.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales")
@Tag(name = "Vendite", description = "Consultazione paginata e registrazione delle vendite")
public class SaleApiController {

    private final SaleService sales;
    private final KpiService kpi;

    public SaleApiController(SaleService sales, KpiService kpi) {
        this.sales = sales;
        this.kpi = kpi;
    }

    @GetMapping
    @Operation(summary = "Vendite del periodo, dalla più recente")
    public PageResponse<SaleResponse> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return sales.search(kpi.resolveRange(from, to), storeId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una vendita")
    public SaleResponse register(@Valid @RequestBody SaleRequest request) {
        return sales.register(request);
    }
}
