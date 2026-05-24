--liquibase formatted sql

--changeset dev:add-book-category-assignments
CREATE TABLE IF NOT EXISTS book_category_assignments (
    book_id     BIGINT NOT NULL REFERENCES books(id)           ON DELETE CASCADE,
    category_id UUID   NOT NULL REFERENCES book_categories(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, category_id)
);
--rollback DROP TABLE IF EXISTS book_category_assignments;
