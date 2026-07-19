-- V8__create_account_operations_table.sql
-- Migration #8: create table account_operations

-- Creating table 'account_operations' for storing main user data
CREATE TABLE IF NOT EXISTS account_operations (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,

    amount            NUMERIC(18, 2) NOT NULL,
    operation_type    VARCHAR(20) NOT NULL, -- 'DEPOSIT' или 'WITHDRAWAL'
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now(),

    CONSTRAINT fk_operations_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

