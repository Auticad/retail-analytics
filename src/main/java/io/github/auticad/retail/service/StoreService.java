package io.github.auticad.retail.service;

import java.util.List;

import io.github.auticad.retail.domain.District;
import io.github.auticad.retail.domain.Store;
import io.github.auticad.retail.dto.StoreRequest;
import io.github.auticad.retail.dto.StoreResponse;
import io.github.auticad.retail.repository.DistrictRepository;
import io.github.auticad.retail.repository.SaleRepository;
import io.github.auticad.retail.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository stores;
    private final DistrictRepository districts;
    private final SaleRepository sales;

    // Constructor injection: le dipendenze sono esplicite e la classe si testa senza Spring
    public StoreService(StoreRepository stores, DistrictRepository districts, SaleRepository sales) {
        this.stores = stores;
        this.districts = districts;
        this.sales = sales;
    }

    public List<StoreResponse> findAll(Long districtId) {
        List<Store> result = districtId == null
                ? stores.findAllByOrderByCodeAsc()
                : stores.findByDistrictIdOrderByCodeAsc(districtId);
        return result.stream().map(StoreResponse::from).toList();
    }

    public StoreResponse findById(Long id) {
        return StoreResponse.from(load(id));
    }

    @Transactional
    public StoreResponse create(StoreRequest request) {
        String code = normalizeCode(request.code());
        if (stores.existsByCodeIgnoreCase(code)) {
            throw new ConflictException("code", "Esiste già un negozio con codice " + code);
        }
        Store store = new Store(code, request.name().trim(), request.city().trim(),
                request.province().trim().toUpperCase(), loadDistrict(request.districtId()),
                request.sqm(), request.openedOn(), request.active());
        return StoreResponse.from(stores.save(store));
    }

    @Transactional
    public StoreResponse update(Long id, StoreRequest request) {
        Store store = load(id);
        String code = normalizeCode(request.code());
        if (stores.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new ConflictException("code", "Esiste già un negozio con codice " + code);
        }
        store.setCode(code);
        store.setName(request.name().trim());
        store.setCity(request.city().trim());
        store.setProvince(request.province().trim().toUpperCase());
        store.setDistrict(loadDistrict(request.districtId()));
        store.setSqm(request.sqm());
        store.setOpenedOn(request.openedOn());
        store.setActive(request.active());
        // nessuna save(): l'entità è gestita, Hibernate scrive le modifiche al commit (dirty checking)
        return StoreResponse.from(store);
    }

    @Transactional
    public void delete(Long id) {
        Store store = load(id);
        if (sales.existsByStoreId(id)) {
            throw new ConflictException(null,
                    "Il negozio " + store.getCode() + " ha vendite registrate: disattivalo invece di eliminarlo");
        }
        stores.delete(store);
    }

    private Store load(Long id) {
        return stores.findWithDistrictById(id)
                .orElseThrow(() -> new NotFoundException("Negozio " + id + " non trovato"));
    }

    private District loadDistrict(Long districtId) {
        return districts.findById(districtId)
                .orElseThrow(() -> new NotFoundException("Distretto " + districtId + " non trovato"));
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }
}
