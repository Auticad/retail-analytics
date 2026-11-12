package io.github.auticad.retail.service;

import java.time.LocalDate;
import java.util.List;

import io.github.auticad.retail.dto.CategorySales;
import io.github.auticad.retail.dto.DateRange;
import io.github.auticad.retail.dto.DistrictSales;
import io.github.auticad.retail.dto.KpiSummary;
import io.github.auticad.retail.dto.MonthlySales;
import io.github.auticad.retail.dto.ProductRanking;
import io.github.auticad.retail.dto.StorePerformance;
import io.github.auticad.retail.repository.KpiRepository;
import io.github.auticad.retail.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KpiService {

    private final KpiRepository kpi;
    private final SaleRepository sales;

    public KpiService(KpiRepository kpi, SaleRepository sales) {
        this.kpi = kpi;
        this.sales = sales;
    }

    /**
     * Completa il periodo richiesto. Senza estremi, il default sono gli ultimi 12 mesi che terminano
     * con l'ultima vendita registrata: la dashboard non resta vuota se i dati non arrivano fino a oggi.
     */
    public DateRange resolveRange(LocalDate from, LocalDate to) {
        LocalDate end = to != null ? to : sales.findLatestSaleDate().orElseGet(LocalDate::now);
        LocalDate start = from != null ? from : end.minusMonths(12).plusDays(1);
        return new DateRange(start, end);
    }

    public KpiSummary summary(DateRange range) {
        return kpi.summary(range.from(), range.to());
    }

    public List<MonthlySales> monthly(DateRange range) {
        return kpi.monthly(range.from(), range.to());
    }

    public List<StorePerformance> storePerformance(DateRange range) {
        return kpi.storePerformance(range.from(), range.to());
    }

    public List<DistrictSales> byDistrict(DateRange range) {
        return kpi.byDistrict(range.from(), range.to());
    }

    public List<CategorySales> byCategory(DateRange range) {
        return kpi.byCategory(range.from(), range.to());
    }

    public List<ProductRanking> topProducts(DateRange range, int limit) {
        return kpi.topProducts(range.from(), range.to(), Math.clamp(limit, 1, 50));
    }
}
