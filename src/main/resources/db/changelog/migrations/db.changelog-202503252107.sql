--liquibase formatted sql
--changeset vitor:202503252107
--comment: boards table create
CREATE TABLE boards(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;



--rollback DROP TABLE boards;
