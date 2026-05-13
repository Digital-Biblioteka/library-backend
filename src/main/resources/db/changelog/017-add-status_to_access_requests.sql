--liquibase formatted sql
--changeset Alina:add-groups
ALTER TABLE access_requests
ADD status VARCHAR(255) NOT NULL default 'PENDING';
--rollback ALTER TABLE access_requests DROP COLUMN status;