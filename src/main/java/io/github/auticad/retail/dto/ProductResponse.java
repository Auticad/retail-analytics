package io.github.auticad.retail.dto;

import java.math.BigDecimal;

import io.github.auticad.retail.domain.Product;

public record ProductResponse(Long id, String sku, String name, String category,
                              BigDecimal listPrice, BigDecimal unitCost) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getSku(), product.getName(),
                product.getCategory().getName(), product.getListPrice(), product.getUnitCost());
    }
}
