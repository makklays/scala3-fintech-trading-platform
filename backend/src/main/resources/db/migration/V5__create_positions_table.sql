-- V5__create_positions_table.sql
-- Migration #5: create table positions

-- Creating table 'positions' for storing main user data
-- Таблица открытых и закрытых позиций
CREATE TABLE IF NOT EXISTS positions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    instrument       VARCHAR(20) NOT NULL,                 -- 'EUR_USD', 'GBP_USD', 'XAU_USD' (Золото)
    quantity         NUMERIC(10, 2) NOT NULL,              -- Объем в лотах (напр. 1.00 или 0.10)
    leverage         INTEGER NOT NULL DEFAULT 100,         -- Плечо (1:100)
    side             VARCHAR(4) NOT NULL,                  -- 'BUY' (Long) или 'SELL' (Short)
    entry_price      NUMERIC(18, 5) NOT NULL,              -- Forex требует до 5 знаков после запятой
    margin_required  NUMERIC(18, 2) NOT NULL,              -- Залог под позицию
    status           VARCHAR(10) NOT NULL DEFAULT 'OPEN',  -- 'OPEN', 'CLOSED'

    opened_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    closed_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_positions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for faster lookups
CREATE INDEX idx_positions_user_status ON positions(user_id, status);

