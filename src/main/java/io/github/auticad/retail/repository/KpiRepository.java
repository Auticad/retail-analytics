package io.github.auticad.retail.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import io.github.auticad.retail.dto.CategorySales;
import io.github.auticad.retail.dto.DistrictSales;
import io.github.auticad.retail.dto.KpiSummary;
import io.github.auticad.retail.dto.MonthlySales;
import io.github.auticad.retail.dto.ProductRanking;
import io.github.auticad.retail.dto.StorePerformance;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Query analitiche in SQL nativo. JPA resta lo strumento per il CRUD; per aggregazioni e join
 * di reporting l'SQL esplicito è più leggibile e si controlla meglio.
 */
@Repository
public class KpiRepository {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final JdbcClient jdbc;

    public KpiRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public KpiSummary summary(LocalDate from, LocalDate to) {
        String sql = """
                SELECT COALESCE(SUM(amount), 0)               AS revenue,
                       COALESCE(SUM(amount - cost_amount), 0) AS margin,
                       COALESCE(SUM(quantity), 0)             AS units,
                       COUNT(*)                               AS sale_rows
                FROM sale
                WHERE sale_date BETWEEN :from AND :to
                """;
        return jdbc.sql(sql)
                .param("from", from)
                .param("to", to)
                .query((rs, rowNum) -> {
                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    BigDecimal margin = rs.getBigDecimal("margin");
                    long saleRows = rs.getLong("sale_rows");
                    BigDecimal average = saleRows == 0
                            ? BigDecimal.ZERO
                            : revenue.divide(BigDecimal.valueOf(saleRows), 2, RoundingMode.HALF_UP);
                    return new KpiSummary(from, to, revenue, margin, percent(margin, revenue),
                            rs.getLong("units"), saleRows, average);
                })
                .single();
    }

    public List<MonthlySales> monthly(LocalDate from, LocalDate to) {
        String sql = """
                SELECT m.month_label,
                       m.revenue,
                       m.margin,
                       m.units,
                       COALESCE(t.target, 0) AS target
                FROM (
                    SELECT d.month_label, d.month_start,
                           SUM(sa.amount)                  AS revenue,
                           SUM(sa.amount - sa.cost_amount) AS margin,
                           SUM(sa.quantity)                AS units
                    FROM sale sa
                    JOIN dim_date d ON d.date_value = sa.sale_date
                    WHERE sa.sale_date BETWEEN :from AND :to
                    GROUP BY d.month_label, d.month_start
                ) m
                LEFT JOIN (
                    SELECT target_month, SUM(target_amount) AS target
                    FROM monthly_target
                    GROUP BY target_month
                ) t ON t.target_month = m.month_start
                ORDER BY m.month_start
                """;
        return jdbc.sql(sql)
                .param("from", from)
                .param("to", to)
                .query((rs, rowNum) -> new MonthlySales(
                        rs.getString("month_label"),
                        rs.getBigDecimal("revenue"),
                        rs.getBigDecimal("margin"),
                        rs.getLong("units"),
                        rs.getBigDecimal("target")))
                .list();
    }

    public List<StorePerformance> storePerformance(LocalDate from, LocalDate to) {
        String sql = """
                SELECT s.id, s.code, s.name, s.sqm, d.name AS district,
                       COALESCE(v.revenue, 0) AS revenue,
                       COALESCE(v.margin, 0)  AS margin,
                       COALESCE(t.target, 0)  AS target
                FROM store s
                JOIN district d ON d.id = s.district_id
                LEFT JOIN (
                    SELECT store_id,
                           SUM(amount)               AS revenue,
                           SUM(amount - cost_amount) AS margin
                    FROM sale
                    WHERE sale_date BETWEEN :from AND :to
                    GROUP BY store_id
                ) v ON v.store_id = s.id
                LEFT JOIN (
                    SELECT store_id, SUM(target_amount) AS target
                    FROM monthly_target
                    WHERE target_month BETWEEN :fromMonth AND :to
                    GROUP BY store_id
                ) t ON t.store_id = s.id
                ORDER BY revenue DESC, s.code
                """;
        return jdbc.sql(sql)
                .param("from", from)
                .param("to", to)
                .param("fromMonth", from.withDayOfMonth(1))
                .query((rs, rowNum) -> {
                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    BigDecimal margin = rs.getBigDecimal("margin");
                    BigDecimal target = rs.getBigDecimal("target");
                    BigDecimal perSqm = revenue.divide(BigDecimal.valueOf(rs.getInt("sqm")), 2, RoundingMode.HALF_UP);
                    return new StorePerformance(rs.getLong("id"), rs.getString("code"), rs.getString("name"),
                            rs.getString("district"), revenue, margin, percent(margin, revenue),
                            target, percent(revenue, target), perSqm);
                })
                .list();
    }

    public List<DistrictSales> byDistrict(LocalDate from, LocalDate to) {
        String sql = """
                SELECT d.name, d.manager,
                       COUNT(DISTINCT s.id)                       AS stores,
                       COALESCE(SUM(sa.amount), 0)                  AS revenue,
                       COALESCE(SUM(sa.amount - sa.cost_amount), 0) AS margin
                FROM district d
                JOIN store s ON s.district_id = d.id
                LEFT JOIN sale sa ON sa.store_id = s.id AND sa.sale_date BETWEEN :from AND :to
                GROUP BY d.id, d.name, d.manager
                ORDER BY revenue DESC
                """;
        return jdbc.sql(sql)
                .param("from", from)
                .param("to", to)
                .query((rs, rowNum) -> {
                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    BigDecimal margin = rs.getBigDecimal("margin");
                    return new DistrictSales(rs.getString("name"), rs.getString("manager"), rs.getInt("stores"),
                            revenue, margin, percent(margin, revenue));
                })
                .list();
    }

    public List<CategorySales> byCategory(LocalDate from, LocalDate to) {
        String sql = """
                SELECT c.name,
                       SUM(sa.amount)                  AS revenue,
                       SUM(sa.amount - sa.cost_amount) AS margin,
                       SUM(sa.quantity)                AS units
                FROM sale sa
                JOIN product p ON p.id = sa.product_id
                JOIN category c ON c.id = p.category_id
                WHERE sa.sale_date BETWEEN :from AND :to
                GROUP BY c.id, c.name
                ORDER BY revenue DESC
                """;
        return jdbc.sql(sql)
                .param("from", from)
                .param("to", to)
                .query((rs, rowNum) -> {
                    BigDecimal revenue = rs.getBigDecimal("revenue");
                    BigDecimal margin = rs.getBigDecimal("margin");
                    return new CategorySales(rs.getString("name"), revenue, margin,
                            percent(margin, revenue), rs.getLong("units"));
                })
                .list();
    }

    public List<ProductRanking> topProducts(LocalDate from, LocalDate to, int limit) {
        String sql = """
                SELECT p.sku, p.name, c.name AS category,
                       SUM(sa.quantity) AS units,
                       SUM(sa.amount)   AS revenue
                FROM sale sa
                JOIN product p ON p.id = sa.product_id
                JOIN category c ON c.id = p.category_id
                WHERE sa.sale_date BETWEEN :from AND :to
                GROUP BY p.id, p.sku, p.name, c.name
                ORDER BY revenue DESC
                LIMIT :limit
                """;
        return jdbc.sql(sql)
                .param("from", from)
                .param("to", to)
                .param("limit", limit)
                .query((rs, rowNum) -> new ProductRanking(rs.getString("sku"), rs.getString("name"),
                        rs.getString("category"), rs.getLong("units"), rs.getBigDecimal("revenue")))
                .list();
    }

    /** part / total in percentuale con una cifra decimale; zero se il totale è zero. */
    private static BigDecimal percent(BigDecimal part, BigDecimal total) {
        if (total == null || total.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(HUNDRED).divide(total, 1, RoundingMode.HALF_UP);
    }
}
