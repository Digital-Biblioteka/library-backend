--liquibase formatted sql

--changeset Alina:add-bookmark-groups
ALTER TABLE bookmarks
ADD group_id UUID REFERENCES bookmark_groups(id);
--rollback ALTER TABLE bookmarks DROP COLUMN group_id;
