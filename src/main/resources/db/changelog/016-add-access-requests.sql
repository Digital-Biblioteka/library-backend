--liquibase formatted sql
--changeset Alina:add-groups
CREATE TABLE IF NOT EXISTS access_requests(
    id uuid NOT NULL PRIMARY KEY,
    book_id bigint NOT NULL REFERENCES books(id),
    user_id bigint NOT NULL REFERENCES users(id),
    group_id uuid NOT NULL REFERENCES groups(id)
)
--rollback DROP TABLE IF EXISTS access_requests;