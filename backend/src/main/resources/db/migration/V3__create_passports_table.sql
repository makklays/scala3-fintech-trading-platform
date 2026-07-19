-- V3__create_passports_table.sql
-- Migration #3: create table passports

-- Creating table 'passports' for saving main data of passport
CREATE TABLE IF NOT EXISTS passports
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    number       VARCHAR(50) NOT NULL,
    date_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    who_gave_it  VARCHAR(255),

    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Indexes for faster lookups
CREATE INDEX idx_passports_user_id ON passports(user_id);
CREATE INDEX idx_passports_number ON passports(number);

