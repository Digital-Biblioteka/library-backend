--liquibase formatted sql
--changeset Alina:add-timestamp-last-read
ALTER TABLE last_read_books
ADD COLUMN last_read_at TIMESTAMP default now();
--rollback ALTER TABLE last_read_books
-- DROP COLUMN last_read_at;
