package io.github.auticad.retail.web;

import java.time.LocalDate;

import io.github.auticad.retail.dto.StoreRequest;
import io.github.auticad.retail.dto.StoreResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Oggetto di appoggio del form HTML. È un JavaBean classico (costruttore vuoto, getter e setter)
 * perché il data binding di Spring MVC e th:field di Thymeleaf lavorano sulle proprietà.
 */
public class StoreForm {

    @NotBlank(message = "Il codice è obbligatorio")
    @Size(max = 10, message = "Massimo 10 caratteri")
    private String code;

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 80, message = "Massimo 80 caratteri")
    private String name;

    @NotBlank(message = "La città è obbligatoria")
    @Size(max = 60, message = "Massimo 60 caratteri")
    private String city;

    @NotBlank(message = "La provincia è obbligatoria")
    @Size(min = 2, max = 2, message = "Sigla di 2 lettere")
    private String province;

    @NotNull(message = "Scegli un distretto")
    private Long districtId;

    @NotNull(message = "La superficie è obbligatoria")
    @Positive(message = "Deve essere maggiore di zero")
    private Integer sqm;

    @NotNull(message = "La data di apertura è obbligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate openedOn;

    private boolean active = true;

    public static StoreForm from(StoreResponse store) {
        StoreForm form = new StoreForm();
        form.setCode(store.code());
        form.setName(store.name());
        form.setCity(store.city());
        form.setProvince(store.province());
        form.setDistrictId(store.districtId());
        form.setSqm(store.sqm());
        form.setOpenedOn(store.openedOn());
        form.setActive(store.active());
        return form;
    }

    public StoreRequest toRequest() {
        return new StoreRequest(code, name, city, province, districtId, sqm, openedOn, active);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Integer getSqm() {
        return sqm;
    }

    public void setSqm(Integer sqm) {
        this.sqm = sqm;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public void setOpenedOn(LocalDate openedOn) {
        this.openedOn = openedOn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
