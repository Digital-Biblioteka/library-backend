--liquibase formatted sql

--changeset Alina:add-bookmark-groups
CREATE TABLE bookmark_groups(
    id UUID PRIMARY KEY ,
    book_id BIGINT NOT NULL REFERENCES books(id),
    owner_id BIGINT NOT NULL REFERENCES users(id),
    access_token UUID NOT NULL ,
    name VARCHAR(255),
    visibility VARCHAR(255) NOT NULL ,
    created_at TIMESTAMP DEFAULT now()
);
--rollback DROP TABLE bookmark_groups;

--changeset Alina:add-bookmarkgroups-users-relations
CREATE TABLE bookmark_group_user(
    user_id BIGINT NOT NULL REFERENCES users(id),
    group_id UUID NOT NULL REFERENCES bookmark_groups(id),
    PRIMARY KEY(user_id, group_id)
);
--rollback DROP TABLE bookmark_group_user;