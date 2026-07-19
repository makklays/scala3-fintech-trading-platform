-- V7__create_instruments_table.sql
-- Migration #7: create table instruments

-- Creating table 'instruments' for storing main user data
CREATE TABLE IF NOT EXISTS instruments (
    symbol         VARCHAR(20) PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    lot_size       INTEGER NOT NULL DEFAULT 100000, -- 1 лот EUR/USD = 100,000 базовой валюты
    base_price     NUMERIC(18, 5) NOT NULL,
    max_leverage   INTEGER NOT NULL DEFAULT 500
);

