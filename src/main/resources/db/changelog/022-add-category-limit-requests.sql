--liquibase formatted sql

--changeset dev:add-category-limit-requests
CREATE TABLE IF NOT EXISTS category_limit_requests (
    id              UUID         PRIMARY KEY,
    group_id        UUID         NOT NULL REFERENCES groups(id)          ON DELETE CASCADE,
    category_id     UUID         NOT NULL REFERENCES book_categories(id) ON DELETE CASCADE,
    requested_limit BIGINT       NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING'
);
--rollback DROP TABLE IF EXISTS category_limit_requests;
