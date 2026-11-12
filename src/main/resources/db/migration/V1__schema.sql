-- V1: schema operativo (OLTP) + tabelle di supporto all'analisi

CREATE TABLE district (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    code    VARCHAR(10)  NOT NULL,
    name    VARCHAR(80)  NOT NULL,
    manager VARCHAR(80)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_district_code UNIQUE (code)
);

CREATE TABLE store (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(10)  NOT NULL,
    name        VARCHAR(80)  NOT NULL,
    city        VARCHAR(60)  NOT NULL,
    province    VARCHAR(2)   NOT NULL,
    district_id BIGINT       NOT NULL,
    sqm         INT          NOT NULL,
    opened_on   DATE         NOT NULL,
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT uk_store_code UNIQUE (code),
    CONSTRAINT fk_store_district FOREIGN KEY (district_id) REFERENCES district (id)
);

CREATE TABLE category (
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_category_name UNIQUE (name)
);

CREATE TABLE product (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    sku         VARCHAR(20)   NOT NULL,
    name        VARCHAR(100)  NOT NULL,
    category_id BIGINT        NOT NULL,
    list_price  DECIMAL(10,2) NOT NULL,
    unit_cost   DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_product_sku UNIQUE (sku),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
);

-- Una riga = un prodotto venduto in un negozio in un giorno (grana del fatto)
CREATE TABLE sale (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    sale_date   DATE          NOT NULL,
    store_id    BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    quantity    INT           NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    cost_amount DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sale_store FOREIGN KEY (store_id) REFERENCES store (id),
    CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES product (id),
    INDEX ix_sale_date (sale_date),
    INDEX ix_sale_store_date (store_id, sale_date)
);

-- Obiettivo di fatturato mensile per negozio
CREATE TABLE monthly_target (
    store_id      BIGINT        NOT NULL,
    target_month  DATE          NOT NULL,
    target_amount DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (store_id, target_month),
    CONSTRAINT fk_target_store FOREIGN KEY (store_id) REFERENCES store (id)
);

-- Tabella calendario: serve sia alle query KPI sia a Power BI come dimensione Data
CREATE TABLE dim_date (
    date_key       INT         NOT NULL,
    date_value     DATE        NOT NULL,
    year_number    INT         NOT NULL,
    quarter_number INT         NOT NULL,
    month_number   INT         NOT NULL,
    month_name     VARCHAR(12) NOT NULL,
    month_label    VARCHAR(7)  NOT NULL,
    month_start    DATE        NOT NULL,
    weekday_number INT         NOT NULL,
    weekday_name   VARCHAR(12) NOT NULL,
    is_weekend     TINYINT(1)  NOT NULL,
    PRIMARY KEY (date_key),
    CONSTRAINT uk_dim_date_value UNIQUE (date_value)
);
