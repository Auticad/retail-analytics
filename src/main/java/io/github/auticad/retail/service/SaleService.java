package io.github.auticad.retail.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import io.github.auticad.retail.domain.Product;
import io.github.auticad.retail.domain.Sale;
import io.github.auticad.retail.domain.Store;
import io.github.auticad.retail.dto.DateRange;
import io.github.auticad.retail.dto.PageResponse;
import io.github.auticad.retail.dto.SaleRequest;
import io.github.auticad.retail.dto.SaleResponse;
import io.github.auticad.retail.repository.ProductRepository;
import io.github.auticad.retail.repository.SaleRepository;
import io.github.auticad.retail.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SaleService {

    static final int MAX_PAGE_SIZE = 200;

    private final SaleRepository sales;
    private final StoreRepository stores;
    private final ProductRepository products;

    public SaleService(SaleRepository sales, StoreRepository stores, ProductRepository products) {
        this.sales = sales;
        this.stores = stores;
        this.products = products;
    }

    public PageResponse<SaleResponse> search(DateRange range, Long storeId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE),
                Sort.by(Sort.Order.desc("saleDate"), Sort.Order.desc("id")));
        Page<Sale> result = storeId == null
                ? sales.findBySaleDateBetween(range.from(), range.to(), pageable)
                : sales.findByStoreIdAndSaleDateBetween(storeId, range.from(), range.to(), pageable);
        return PageResponse.from(result, SaleResponse::from);
    }

    @Transactional
    public SaleResponse register(SaleRequest request) {
        Store store = stores.findById(request.storeId())
                .orElseThrow(() -> new NotFoundException("Negozio " + request.storeId() + " non trovato"));
        Product product = products.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Prodotto " + request.productId() + " non trovato"));

        if (!store.isActive()) {
            throw new ConflictException("storeId", "Il negozio " + store.getCode() + " non è attivo");
        }
        if (request.saleDate().isBefore(store.getOpenedOn())) {
            throw new ConflictException("saleDate",
                    "La data precede l'apertura del negozio (" + store.getOpenedOn() + ")");
        }
        if (request.saleDate().isAfter(LocalDate.now())) {
            throw new ConflictException("saleDate", "La data di vendita non può essere futura");
        }

        BigDecimal unitPrice = (request.unitPrice() != null ? request.unitPrice() : product.getListPrice())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal quantity = BigDecimal.valueOf(request.quantity());
        BigDecimal amount = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        BigDecimal costAmount = product.getUnitCost().multiply(quantity).setScale(2, RoundingMode.HALF_UP);

        Sale sale = new Sale(request.saleDate(), store, product, request.quantity(), unitPrice, amount, costAmount);
        return SaleResponse.from(sales.save(sale));
    }
}
