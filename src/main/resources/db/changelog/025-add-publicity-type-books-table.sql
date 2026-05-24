--liquibase formatted sql

--changeset Alina:add-category-permissions
ALTER TABLE books
ADD COLUMN publicity VARCHAR(255) NOT NULL default 'PUBLIC'
