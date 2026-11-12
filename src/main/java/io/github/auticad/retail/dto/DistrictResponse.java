package io.github.auticad.retail.dto;

import io.github.auticad.retail.domain.District;

public record DistrictResponse(Long id, String code, String name, String manager) {

    public static DistrictResponse from(District district) {
        return new DistrictResponse(district.getId(), district.getCode(), district.getName(), district.getManager());
    }
}
