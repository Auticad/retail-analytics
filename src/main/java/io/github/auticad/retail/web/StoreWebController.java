package io.github.auticad.retail.web;

import io.github.auticad.retail.service.CatalogService;
import io.github.auticad.retail.service.ConflictException;
import io.github.auticad.retail.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stores")
public class StoreWebController {

    private final StoreService stores;
    private final CatalogService catalog;

    public StoreWebController(StoreService stores, CatalogService catalog) {
        this.stores = stores;
        this.catalog = catalog;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stores", stores.findAll(null));
        return "stores/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new StoreForm());
        return showForm(model, null);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") StoreForm form, BindingResult binding,
                         Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return showForm(model, null);
        }
        try {
            stores.create(form.toRequest());
        } catch (ConflictException ex) {
            reject(binding, ex);
            return showForm(model, null);
        }
        // Post/Redirect/Get: dopo una POST riuscita si reindirizza, così un F5 non reinvia il form
        redirect.addFlashAttribute("message", "Negozio creato");
        return "redirect:/stores";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", StoreForm.from(stores.findById(id)));
        return showForm(model, id);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") StoreForm form,
                         BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return showForm(model, id);
        }
        try {
            stores.update(id, form.toRequest());
        } catch (ConflictException ex) {
            reject(binding, ex);
            return showForm(model, id);
        }
        redirect.addFlashAttribute("message", "Negozio aggiornato");
        return "redirect:/stores";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            stores.delete(id);
            redirect.addFlashAttribute("message", "Negozio eliminato");
        } catch (ConflictException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/stores";
    }

    private String showForm(Model model, Long storeId) {
        model.addAttribute("storeId", storeId);
        model.addAttribute("districts", catalog.districts());
        return "stores/form";
    }

    private static void reject(BindingResult binding, ConflictException ex) {
        if (ex.getField() != null) {
            binding.rejectValue(ex.getField(), "conflict", ex.getMessage());
        } else {
            binding.reject("conflict", ex.getMessage());
        }
    }
}
