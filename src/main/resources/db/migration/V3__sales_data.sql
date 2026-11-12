-- V3: vendite di esempio dal 2025-01-01 al 2026-10-31.
-- I dati sono deterministici: CRC32 fa da generatore pseudo-casuale ripetibile,
-- cosi' ogni installazione (e ogni test) ottiene esattamente le stesse righe.

INSERT INTO sale (sale_date, store_id, product_id, quantity, unit_price, amount, cost_amount)
SELECT x.date_value,
       x.store_id,
       x.product_id,
       x.quantity,
       x.unit_price,
       ROUND(x.quantity * x.unit_price, 2),
       ROUND(x.quantity * x.unit_cost, 2)
FROM (
    SELECT d.date_value,
           s.id AS store_id,
           p.id AS product_id,
           p.unit_cost,
           1 + (CRC32(CONCAT('q-', d.date_key, '-', s.id, '-', p.id)) % 4) AS quantity,
           -- un articolo su dieci e' venduto al -20%, uno su dieci al -10%
           ROUND(p.list_price * (CASE CRC32(CONCAT('d-', d.date_key, '-', s.id, '-', p.id)) % 10
                                     WHEN 0 THEN 0.80
                                     WHEN 1 THEN 0.90
                                     ELSE 1.00
                                 END), 2) AS unit_price
    FROM dim_date d
    CROSS JOIN store s
    CROSS JOIN product p
    WHERE d.date_value BETWEEN DATE('2025-01-01') AND DATE('2026-10-31')
      AND d.date_value >= s.opened_on
      -- probabilita' di vendita: cresce con la superficie, nel weekend, a dicembre e nei mesi di saldi
      AND (CRC32(CONCAT('s-', d.date_key, '-', s.id, '-', p.id)) % 100)
          < (10 + (s.sqm DIV 100)
                + IF(d.is_weekend = 1, 6, 0)
                + IF(d.month_number = 12, 8, 0)
                + IF(d.month_number IN (1, 7), 4, 0))
) x;

-- Obiettivi mensili: fatturato reale del mese corretto di un fattore fra 0,92 e 1,11
INSERT INTO monthly_target (store_id, target_month, target_amount)
SELECT m.store_id,
       m.month_start,
       ROUND(m.revenue * (0.92 + (CRC32(CONCAT('t-', m.store_id, '-', m.month_start)) % 20) / 100), -2)
FROM (
    SELECT sa.store_id, d.month_start, SUM(sa.amount) AS revenue
    FROM sale sa
    JOIN dim_date d ON d.date_value = sa.sale_date
    GROUP BY sa.store_id, d.month_start
) m;
