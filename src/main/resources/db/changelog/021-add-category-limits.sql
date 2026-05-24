--liquibase formatted sql

--changeset dev:add-category-limits
CREATE TABLE IF NOT EXISTS category_limits (
    id          BIGSERIAL PRIMARY KEY,
    group_id    UUID   NOT NULL REFERENCES groups(id)          ON DELETE CASCADE,
    category_id UUID   NOT NULL REFERENCES book_categories(id) ON DELETE CASCADE,
    limit_num   BIGINT NOT NULL DEFAULT 0,
    UNIQUE (group_id, category_id)
);
--rollback DROP TABLE IF EXISTS category_limits;
