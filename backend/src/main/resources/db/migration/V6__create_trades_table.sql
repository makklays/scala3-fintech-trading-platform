-- V6__create_trades_table.sql
-- Migration #6: create table trades

-- Creating table 'trades' for storing main user data
-- Таблица зафиксированной истории торгов (Закрытые позиции)
CREATE TABLE IF NOT EXISTS trades (
    id              BIGSERIAL PRIMARY KEY,
    position_id     BIGINT NOT NULL,             -- ID позиции, к которой относится сделка
    user_id         BIGINT NOT NULL,

    instrument      VARCHAR(20) NOT NULL,        -- Наименование инструмента (например, GBP_USD)
    side            VARCHAR(4) NOT NULL,         -- 'BUY' или 'SELL'
    quantity        NUMERIC(10, 2) NOT NULL,     -- Количество купленных/проданных единиц инструмента
    entry_price     NUMERIC(18, 5) NOT NULL,     -- Цена входа в позицию
    exit_price      NUMERIC(18, 5) NOT NULL,     -- Цена выхода из позиции

    realized_pnl    NUMERIC(18, 2) NOT NULL,     -- Итоговый профит/убыток
    executed_at     TIMESTAMP WITH TIME ZONE DEFAULT now(),

    CONSTRAINT fk_trades_position FOREIGN KEY (position_id) REFERENCES positions(id),
    CONSTRAINT fk_trades_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes for faster lookups
CREATE INDEX idx_trades_user ON trades(user_id);

