--liquibase formatted sql

--changeset dev:add-category-permissions
CREATE TABLE IF NOT EXISTS category_permissions (
    id           BIGSERIAL  PRIMARY KEY,
    user_id      BIGINT     NOT NULL REFERENCES users(id)            ON DELETE CASCADE,
    group_id     UUID       NOT NULL REFERENCES groups(id)           ON DELETE CASCADE,
    category_id  UUID       NOT NULL REFERENCES book_categories(id)  ON DELETE CASCADE,
    time_expires TIMESTAMP  NOT NULL,
    UNIQUE (user_id, group_id, category_id)
);
--rollback DROP TABLE IF EXISTS category_permissions;
