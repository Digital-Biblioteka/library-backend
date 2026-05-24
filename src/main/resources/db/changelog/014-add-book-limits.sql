--liquibase formatted sql
--changeset Alina:add-book-limits
CREATE TABLE IF NOT EXISTS book_limits(
    id BIGSERIAL PRIMARY KEY ,
    group_id uuid NOT NULL REFERENCES groups(id),
    book_id int8 NOT NULL REFERENCES books(id),
    limit_num int8 NOT NULL,
    UNIQUE(group_id, book_id)
);
--rollback drop table book_limits;
