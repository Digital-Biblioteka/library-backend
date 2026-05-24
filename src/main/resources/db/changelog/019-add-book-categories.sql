--liquibase formatted sql

--changeset dev:add-book-categories
CREATE TABLE IF NOT EXISTS book_categories (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_by  BIGINT       REFERENCES users(id) ON DELETE SET NULL
);
--rollback DROP TABLE IF EXISTS book_categories;
