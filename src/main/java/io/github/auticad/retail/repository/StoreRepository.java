package io.github.auticad.retail.repository;

import java.util.List;
import java.util.Optional;

import io.github.auticad.retail.domain.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // @EntityGraph carica il distretto nella stessa query: evita il problema N+1
    @EntityGraph(attributePaths = "district")
    List<Store> findAllByOrderByCodeAsc();

    @EntityGraph(attributePaths = "district")
    List<Store> findByDistrictIdOrderByCodeAsc(Long districtId);

    @EntityGraph(attributePaths = "district")
    Optional<Store> findWithDistrictById(Long id);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
