--liquibase formatted sql
--changeset Alina:add-groups
CREATE TABLE book_limit_requests(
    id uuid PRIMARY KEY ,
    book_id bigint NOT NULL references books(id),
    group_id uuid NOT NULL references groups(id),
    requested_limit bigint NOT NULL ,
    status VARCHAR(50) NOT NULL
);
--rollback DROP TABLE book_limit_requests;