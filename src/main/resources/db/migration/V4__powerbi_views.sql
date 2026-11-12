-- V4: viste a stella per Power BI. Il report legge queste viste, non le tabelle operative:
-- lo schema OLTP puo' cambiare senza rompere il modello semantico.

CREATE VIEW v_dim_date AS
SELECT date_key, date_value, year_number, quarter_number, month_number, month_name,
       month_label, month_start, weekday_number, weekday_name, is_weekend
FROM dim_date;

CREATE VIEW v_dim_store AS
SELECT s.id        AS store_key,
       s.code      AS store_code,
       s.name      AS store_name,
       s.city,
       s.province,
       d.code      AS district_code,
       d.name      AS district_name,
       d.manager   AS district_manager,
       s.sqm,
       s.opened_on,
       s.active
FROM store s
JOIN district d ON d.id = s.district_id;

CREATE VIEW v_dim_product AS
SELECT p.id   AS product_key,
       p.sku,
       p.name AS product_name,
       c.name AS category_name,
       p.list_price,
       p.unit_cost
FROM product p
JOIN category c ON c.id = p.category_id;

CREATE VIEW v_fact_sales AS
SELECT sa.id                                              AS sale_id,
       CAST(DATE_FORMAT(sa.sale_date, '%Y%m%d') AS UNSIGNED) AS date_key,
       sa.store_id                                        AS store_key,
       sa.product_id                                      AS product_key,
       sa.quantity,
       sa.unit_price,
       sa.amount,
       sa.cost_amount,
       sa.amount - sa.cost_amount                         AS margin_amount
FROM sale sa;

CREATE VIEW v_fact_target AS
SELECT t.store_id                                            AS store_key,
       CAST(DATE_FORMAT(t.target_month, '%Y%m%d') AS UNSIGNED) AS date_key,
       t.target_amount
FROM monthly_target t;
