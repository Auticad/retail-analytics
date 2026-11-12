-- V2: anagrafiche di esempio e calendario 2025-2026

INSERT INTO district (code, name, manager) VALUES
    ('D01', 'Milano e Brianza', 'Laura Bianchi'),
    ('D02', 'Lombardia Est',    'Marco Ferri'),
    ('D03', 'Piemonte',         'Elena Costa');

INSERT INTO store (code, name, city, province, district_id, sqm, opened_on, active) VALUES
    ('MI01', 'Milano Duomo',        'Milano',              'MI', 1, 1200, '2018-03-15', 1),
    ('MI02', 'Milano Navigli',      'Milano',              'MI', 1,  650, '2020-09-01', 1),
    ('MB01', 'Monza Centro',        'Monza',               'MB', 1,  800, '2019-05-20', 1),
    ('MB02', 'Vimercate',           'Vimercate',           'MB', 1,  450, '2022-11-10', 1),
    ('MI03', 'Sesto San Giovanni',  'Sesto San Giovanni',  'MI', 1,  500, '2025-09-01', 1),
    ('BG01', 'Bergamo Città Bassa', 'Bergamo',             'BG', 2,  900, '2017-10-05', 1),
    ('BS01', 'Brescia Centro',      'Brescia',             'BS', 2,  850, '2019-02-12', 1),
    ('BS02', 'Desenzano',           'Desenzano del Garda', 'BS', 2,  400, '2021-06-01', 1),
    ('CR01', 'Cremona',             'Cremona',             'CR', 2,  350, '2026-03-02', 1),
    ('TO01', 'Torino Centro',       'Torino',              'TO', 3, 1100, '2016-04-18', 1),
    ('TO02', 'Torino Lingotto',     'Torino',              'TO', 3,  700, '2020-02-03', 1),
    ('NO01', 'Novara',              'Novara',              'NO', 3,  450, '2023-09-25', 1);

INSERT INTO category (name) VALUES
    ('Running'), ('Outdoor'), ('Fitness'), ('Calcio'), ('Nuoto'), ('Accessori');

INSERT INTO product (sku, name, category_id, list_price, unit_cost) VALUES
    ('RUN-001', 'Scarpa running ammortizzata',   1, 129.90,  71.50),
    ('RUN-002', 'Scarpa trail',                  1, 149.90,  84.00),
    ('RUN-003', 'Maglia tecnica running',        1,  34.90,  14.20),
    ('RUN-004', 'Short running',                 1,  29.90,  11.80),
    ('OUT-001', 'Giacca impermeabile 3 strati',  2, 219.00, 118.00),
    ('OUT-002', 'Scarpone trekking',             2, 179.00,  98.50),
    ('OUT-003', 'Zaino 30 litri',                2,  89.90,  42.00),
    ('OUT-004', 'Pile tecnico',                  2,  59.90,  24.50),
    ('FIT-001', 'Tappetino yoga',                3,  24.90,   8.90),
    ('FIT-002', 'Set manubri 2x5 kg',            3,  39.90,  19.00),
    ('FIT-003', 'Leggings fitness',              3,  44.90,  17.50),
    ('FIT-004', 'Fascia elastica',               3,  12.90,   3.80),
    ('CAL-001', 'Scarpa calcio terreno compatto',4,  99.90,  52.00),
    ('CAL-002', 'Pallone gara',                  4,  34.90,  15.00),
    ('CAL-003', 'Parastinchi',                   4,  19.90,   7.20),
    ('CAL-004', 'Maglia allenamento',            4,  27.90,  10.50),
    ('NUO-001', 'Costume gara',                  5,  49.90,  21.00),
    ('NUO-002', 'Occhialini',                    5,  22.90,   8.00),
    ('NUO-003', 'Cuffia silicone',               5,   9.90,   2.60),
    ('NUO-004', 'Accappatoio microfibra',        5,  39.90,  17.80),
    ('ACC-001', 'Borraccia termica',             6,  24.90,   9.40),
    ('ACC-002', 'Calze tecniche 3 paia',         6,  14.90,   5.10),
    ('ACC-003', 'Cappellino',                    6,  17.90,   6.30),
    ('ACC-004', 'Borsone palestra',              6,  44.90,  20.50);

-- Calendario: una riga per giorno, generata incrociando tre serie di cifre (0..999 giorni)
INSERT INTO dim_date (date_key, date_value, year_number, quarter_number, month_number, month_name,
                      month_label, month_start, weekday_number, weekday_name, is_weekend)
SELECT CAST(DATE_FORMAT(cal.dt, '%Y%m%d') AS UNSIGNED),
       cal.dt,
       YEAR(cal.dt),
       QUARTER(cal.dt),
       MONTH(cal.dt),
       ELT(MONTH(cal.dt), 'Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno',
                          'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'),
       DATE_FORMAT(cal.dt, '%Y-%m'),
       DATE_SUB(cal.dt, INTERVAL DAYOFMONTH(cal.dt) - 1 DAY),
       WEEKDAY(cal.dt) + 1,
       ELT(WEEKDAY(cal.dt) + 1, 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato', 'Domenica'),
       IF(WEEKDAY(cal.dt) >= 5, 1, 0)
FROM (
    SELECT DATE_ADD(DATE('2025-01-01'), INTERVAL (u.n + t.n * 10 + h.n * 100) DAY) AS dt
    FROM       (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) u
    CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t
    CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) h
) cal
WHERE cal.dt <= DATE('2026-12-31');
