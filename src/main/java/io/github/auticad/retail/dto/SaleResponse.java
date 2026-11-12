package io.github.auticad.retail.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.github.auticad.retail.domain.Sale;

public record SaleResponse(Long id, LocalDate saleDate, String storeCode, String storeName,
                           String sku, String productName, Integer quantity,
                           BigDecimal unitPrice, BigDecimal amount, BigDecimal margin) {

    public static SaleResponse from(Sale sale) {
        return new SaleResponse(sale.getId(), sale.getSaleDate(),
                sale.getStore().getCode(), sale.getStore().getName(),
                sale.getProduct().getSku(), sale.getProduct().getName(),
                sale.getQuantity(), sale.getUnitPrice(), sale.getAmount(),
                sale.getAmount().subtract(sale.getCostAmount()));
    }
}
