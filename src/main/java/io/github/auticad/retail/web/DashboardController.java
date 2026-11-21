package io.github.auticad.retail.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import io.github.auticad.retail.dto.DateRange;
import io.github.auticad.retail.dto.MonthlySales;
import io.github.auticad.retail.service.KpiService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final KpiService kpi;

    public DashboardController(KpiService kpi) {
        this.kpi = kpi;
    }

    @GetMapping("/")
    public String dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        DateRange range = kpi.resolveRange(from, to);
        model.addAttribute("range", range);
        model.addAttribute("summary", kpi.summary(range));
        model.addAttribute("months", toBars(kpi.monthly(range)));
        model.addAttribute("stores", kpi.storePerformance(range));
        model.addAttribute("districts", kpi.byDistrict(range));
        model.addAttribute("categories", kpi.byCategory(range));
        model.addAttribute("topProducts", kpi.topProducts(range, 5));
        return "dashboard"; // templates/dashboard.html
    }

    /** Il calcolo delle proporzioni resta in Java: il template si limita a disegnare. */
    static List<ChartBar> toBars(List<MonthlySales> months) {
        BigDecimal max = months.stream()
                .map(m -> m.revenue())
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        return months.stream()
                .map(m -> new ChartBar(m.month(), m.revenue(), m.target(),
                        widthPct(m.revenue(), max),
                        m.target().signum() > 0 && m.revenue().compareTo(m.target()) >= 0))
                .toList();
    }

    private static int widthPct(BigDecimal value, BigDecimal max) {
        if (max.signum() == 0) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100)).divide(max, 0, RoundingMode.HALF_UP).intValue();
    }
}
