package io.github.auticad.retail.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import io.github.auticad.retail.domain.Category;
import io.github.auticad.retail.domain.District;
import io.github.auticad.retail.domain.Product;
import io.github.auticad.retail.domain.Sale;
import io.github.auticad.retail.domain.Store;
import io.github.auticad.retail.dto.SaleRequest;
import io.github.auticad.retail.dto.SaleResponse;
import io.github.auticad.retail.repository.ProductRepository;
import io.github.auticad.retail.repository.SaleRepository;
import io.github.auticad.retail.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Test unitario puro: niente Spring, i repository sono mock. Verifica le regole di business della vendita. */
@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    SaleRepository sales;

    @Mock
    StoreRepository stores;

    @Mock
    ProductRepository products;

    SaleService service;

    Store store;
    Product product;

    @BeforeEach
    void setUp() {
        service = new SaleService(sales, stores, products);
        District district = new District("D01", "Milano e Brianza", "Laura Bianchi");
        store = new Store("MB01", "Monza Centro", "Monza", "MB", district, 800, LocalDate.of(2019, 5, 20), true);
        product = new Product("RUN-001", "Scarpa running", new Category("Running"),
                new BigDecimal("129.90"), new BigDecimal("71.50"));
    }

    @Test
    void senzaPrezzoUsaIlListinoECalcolaImportoEMargine() {
        given(stores.findById(1L)).willReturn(Optional.of(store));
        given(products.findById(1L)).willReturn(Optional.of(product));
        given(sales.save(any(Sale.class))).willAnswer(invocation -> invocation.getArgument(0));

        SaleResponse response = service.register(new SaleRequest(LocalDate.of(2026, 10, 15), 1L, 1L, 3, null));

        assertThat(response.unitPrice()).isEqualByComparingTo("129.90");
        assertThat(response.amount()).isEqualByComparingTo("389.70");
        assertThat(response.margin()).isEqualByComparingTo("175.20"); // 389,70 - 3 x 71,50
    }

    @Test
    void conPrezzoScontatoUsaIlPrezzoIndicato() {
        given(stores.findById(1L)).willReturn(Optional.of(store));
        given(products.findById(1L)).willReturn(Optional.of(product));
        given(sales.save(any(Sale.class))).willAnswer(invocation -> invocation.getArgument(0));

        SaleResponse response = service.register(
                new SaleRequest(LocalDate.of(2026, 10, 15), 1L, 1L, 2, new BigDecimal("99.00")));

        assertThat(response.amount()).isEqualByComparingTo("198.00");
        assertThat(response.margin()).isEqualByComparingTo("55.00");
    }

    @Test
    void rifiutaLaVenditaPrimaDellAperturaDelNegozio() {
        given(stores.findById(1L)).willReturn(Optional.of(store));
        given(products.findById(1L)).willReturn(Optional.of(product));

        assertThatThrownBy(() -> service.register(new SaleRequest(LocalDate.of(2018, 1, 10), 1L, 1L, 1, null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("apertura");

        verify(sales, never()).save(any());
    }

    @Test
    void rifiutaLaVenditaSuUnNegozioNonAttivo() {
        store.setActive(false);
        given(stores.findById(1L)).willReturn(Optional.of(store));
        given(products.findById(1L)).willReturn(Optional.of(product));

        assertThatThrownBy(() -> service.register(new SaleRequest(LocalDate.of(2026, 10, 15), 1L, 1L, 1, null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("non è attivo");
    }

    @Test
    void negozioInesistenteSollevaNotFound() {
        given(stores.findById(42L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(new SaleRequest(LocalDate.of(2026, 10, 15), 42L, 1L, 1, null)))
                .isInstanceOf(NotFoundException.class);
    }
}
