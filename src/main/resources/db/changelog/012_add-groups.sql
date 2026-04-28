--liquibase formatted sql

--changeset add-groups
create table groups (
    id UUID PRIMARY KEY ,
    librarian_id int8 REFERENCES users(id),
    name varchar(255),
    description VARCHAR(500)
);
--rollback drop table groups;

--changeset add user-group table
CREATE TABLE user_group(
    user_id int8 NOT NULL REFERENCES users(id),
    group_id uuid NOT NULL REFERENCES groups(id),
    PRIMARY KEY(user_id, group_id)
);

--rollback drop table user_group;
