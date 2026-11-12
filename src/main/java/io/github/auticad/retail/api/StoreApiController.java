package io.github.auticad.retail.api;

import java.net.URI;
import java.util.List;

import io.github.auticad.retail.dto.StoreRequest;
import io.github.auticad.retail.dto.StoreResponse;
import io.github.auticad.retail.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Negozi", description = "CRUD dei punti vendita")
public class StoreApiController {

    private final StoreService stores;

    public StoreApiController(StoreService stores) {
        this.stores = stores;
    }

    @GetMapping
    @Operation(summary = "Elenco dei negozi, filtrabile per distretto")
    public List<StoreResponse> list(@RequestParam(required = false) Long districtId) {
        return stores.findAll(districtId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dettaglio di un negozio")
    public StoreResponse get(@PathVariable Long id) {
        return stores.findById(id);
    }

    @PostMapping
    @Operation(summary = "Crea un negozio")
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody StoreRequest request) {
        StoreResponse created = stores.create(request);
        return ResponseEntity.created(URI.create("/api/v1/stores/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica un negozio")
    public StoreResponse update(@PathVariable Long id, @Valid @RequestBody StoreRequest request) {
        return stores.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un negozio senza vendite")
    public void delete(@PathVariable Long id) {
        stores.delete(id);
    }
}
