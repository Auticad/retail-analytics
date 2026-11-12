package io.github.auticad.retail.api;

import java.util.List;

import io.github.auticad.retail.dto.DistrictResponse;
import io.github.auticad.retail.dto.ProductResponse;
import io.github.auticad.retail.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Anagrafiche", description = "Distretti e prodotti, in sola lettura")
public class CatalogApiController {

    private final CatalogService catalog;

    public CatalogApiController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/districts")
    @Operation(summary = "Elenco dei distretti")
    public List<DistrictResponse> districts() {
        return catalog.districts();
    }

    @GetMapping("/products")
    @Operation(summary = "Elenco dei prodotti con categoria, listino e costo")
    public List<ProductResponse> products() {
        return catalog.products();
    }
}
