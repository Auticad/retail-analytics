package io.github.auticad.retail.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.github.auticad.retail.dto.SaleRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

public class SaleForm {

    @NotNull(message = "La data è obbligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate saleDate = LocalDate.now();

    @NotNull(message = "Scegli un negozio")
    private Long storeId;

    @NotNull(message = "Scegli un prodotto")
    private Long productId;

    @NotNull(message = "La quantità è obbligatoria")
    @Positive(message = "Deve essere maggiore di zero")
    private Integer quantity = 1;

    @Positive(message = "Deve essere maggiore di zero")
    private BigDecimal unitPrice;

    public SaleRequest toRequest() {
        return new SaleRequest(saleDate, storeId, productId, quantity, unitPrice);
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
