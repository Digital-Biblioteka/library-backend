--liquibase formatted sql
--changeset Alina:add-groups
CREATE TABLE IF NOT EXISTS book_permissions(
    id BIGSERIAL PRIMARY KEY ,
    book_id int8 NOT NULL REFERENCES books(id),
    group_id uuid NOT NULL REFERENCES groups(id),
    user_id int8 NOT NULL REFERENCES users(id),
    time_expires TIMESTAMP not null,
    UNIQUE(book_id, group_id, user_id)
);
--rollback drop table book_permissions;
