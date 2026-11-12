package io.github.auticad.retail.web;

import java.time.LocalDate;

import io.github.auticad.retail.dto.DateRange;
import io.github.auticad.retail.service.CatalogService;
import io.github.auticad.retail.service.ConflictException;
import io.github.auticad.retail.service.KpiService;
import io.github.auticad.retail.service.SaleService;
import io.github.auticad.retail.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales")
public class SaleWebController {

    private static final int PAGE_SIZE = 25;

    private final SaleService sales;
    private final StoreService stores;
    private final CatalogService catalog;
    private final KpiService kpi;

    public SaleWebController(SaleService sales, StoreService stores, CatalogService catalog, KpiService kpi) {
        this.sales = sales;
        this.stores = stores;
        this.catalog = catalog;
        this.kpi = kpi;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        DateRange range = kpi.resolveRange(from, to);
        model.addAttribute("range", range);
        model.addAttribute("storeId", storeId);
        model.addAttribute("stores", stores.findAll(null));
        model.addAttribute("salesPage", sales.search(range, storeId, page, PAGE_SIZE));
        return "sales/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new SaleForm());
        return showForm(model);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") SaleForm form, BindingResult binding,
                         Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return showForm(model);
        }
        try {
            sales.register(form.toRequest());
        } catch (ConflictException ex) {
            if (ex.getField() != null) {
                binding.rejectValue(ex.getField(), "conflict", ex.getMessage());
            } else {
                binding.reject("conflict", ex.getMessage());
            }
            return showForm(model);
        }
        redirect.addFlashAttribute("message", "Vendita registrata");
        return "redirect:/sales";
    }

    private String showForm(Model model) {
        model.addAttribute("stores", stores.findAll(null));
        model.addAttribute("products", catalog.products());
        return "sales/form";
    }
}
