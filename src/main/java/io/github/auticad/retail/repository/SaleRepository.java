package io.github.auticad.retail.repository;

import java.time.LocalDate;
import java.util.Optional;

import io.github.auticad.retail.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @EntityGraph(attributePaths = {"store", "product"})
    Page<Sale> findBySaleDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    @EntityGraph(attributePaths = {"store", "product"})
    Page<Sale> findByStoreIdAndSaleDateBetween(Long storeId, LocalDate from, LocalDate to, Pageable pageable);

    boolean existsByStoreId(Long storeId);

    @Query("select max(s.saleDate) from Sale s")
    Optional<LocalDate> findLatestSaleDate();
}
