--liquibase formatted sql

--changeset Alina:add-bookmark-fields
ALTER TABLE bookmarks
    ADD start_offset BIGINT,
    ADD end_offset BIGINT,
    ADD selected_text TEXT,
    ADD color VARCHAR(255);

--rollback ALTER TABLE bookmarks DROP COLUMN start_offset, DROP COLUMN end_offset,
-- DROP COLUMN selected_text, DROP COLUMN color;