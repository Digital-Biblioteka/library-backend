--liquibase formatted sql

--changeset dev:add-category-access-requests
CREATE TABLE IF NOT EXISTS category_access_requests (
    id          UUID        PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users(id)            ON DELETE CASCADE,
    group_id    UUID        NOT NULL REFERENCES groups(id)           ON DELETE CASCADE,
    category_id UUID        NOT NULL REFERENCES book_categories(id)  ON DELETE CASCADE,
    status      VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);
--rollback DROP TABLE IF EXISTS category_access_requests;
