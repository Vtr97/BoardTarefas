--liquibase formatted sql
--changeset vitor:202503252107
--comment: set unblock_reason nullable

ALTER TABLE blocks MODIFY COLUMN unblock_reason VARCHAR(255) NULL;

--rollback ALTER TABLE blocks MODIFY COLUMN unblock_reason VARCHAR(255) NOT NULL;
