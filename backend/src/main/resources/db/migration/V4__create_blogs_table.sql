-- V4__create_blogs_table.sql
-- Migration #4: create table blogs

-- Creating table 'blogs' for saving main data of passport
CREATE TABLE IF NOT EXISTS blogs
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),

    title       VARCHAR(255) NOT NULL,       -- Title of the blog post
    post        TEXT NOT NULL,               -- Content of the blog post

    created_at  TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Indexes for faster lookups
CREATE INDEX idx_blogs_user_id ON blogs(user_id);
CREATE INDEX idx_blogs_title ON blogs(title);

