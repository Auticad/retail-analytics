package io.github.auticad.retail.dto;

import java.time.LocalDate;

import io.github.auticad.retail.domain.Store;

public record StoreResponse(Long id, String code, String name, String city, String province,
                            Long districtId, String districtName, Integer sqm,
                            LocalDate openedOn, boolean active) {

    public static StoreResponse from(Store store) {
        return new StoreResponse(store.getId(), store.getCode(), store.getName(), store.getCity(),
                store.getProvince(), store.getDistrict().getId(), store.getDistrict().getName(),
                store.getSqm(), store.getOpenedOn(), store.isActive());
    }
}
