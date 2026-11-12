package io.github.auticad.retail.service;

import java.util.List;

import io.github.auticad.retail.dto.DistrictResponse;
import io.github.auticad.retail.dto.ProductResponse;
import io.github.auticad.retail.repository.DistrictRepository;
import io.github.auticad.retail.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Anagrafiche in sola lettura: distretti e prodotti. */
@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final DistrictRepository districts;
    private final ProductRepository products;

    public CatalogService(DistrictRepository districts, ProductRepository products) {
        this.districts = districts;
        this.products = products;
    }

    public List<DistrictResponse> districts() {
        return districts.findAll(Sort.by("code")).stream().map(DistrictResponse::from).toList();
    }

    public List<ProductResponse> products() {
        return products.findAllByOrderBySkuAsc().stream().map(ProductResponse::from).toList();
    }
}
