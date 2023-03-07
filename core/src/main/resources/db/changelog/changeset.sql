--liquibase formatted sql

--changeset itanchi.dev@gmail.com:1

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE IF NOT EXISTS `users`
(
    `id`           VARCHAR(36)                      NOT NULL PRIMARY KEY,
    `name`         VARCHAR(255)                     NOT NULL,
    `avatar`       TEXT                             NULL,
    `country_code` VARCHAR(7)                       NOT NULL,
    `phone_number` VARCHAR(12)                      NOT NULL,
    `email`        VARCHAR(255)                     NULL,
    `dob`          DATE                             NULL,
    `gender`       ENUM ('MALE', 'FEMALE', 'OTHER') NULL,
    `is_active`    TINYINT(1)                       NOT NULL DEFAULT 1,
    `is_reported`  TINYINT(1)                       NOT NULL DEFAULT 0,
    `is_blocked`   TINYINT(1)                       NOT NULL DEFAULT 0,
    `is_deleted`   TINYINT(1)                       NOT NULL DEFAULT 0,
    `preferences`  TEXT                             NOT NULL,
    `created_at`   DATETIME                         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME                         NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_country_code_phone_number` ON `users` (`country_code`, `phone_number`);

CREATE INDEX `idx_phone_number` ON `users` (`phone_number`);

CREATE UNIQUE INDEX `idx_email` ON `users` (`email`);

-- -----------------------------------------------------
-- Table `contacts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `contacts`;

CREATE TABLE IF NOT EXISTS `contacts`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `country_code` VARCHAR(7)   NULL,
    `phone_number` VARCHAR(12)  NULL,
    `email`        VARCHAR(255) NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_country_code_phone_number` ON `contacts` (`country_code`, `phone_number`);

CREATE INDEX `idx_phone_number` ON `contacts` (`phone_number`);

CREATE UNIQUE INDEX `idx_email` ON `contacts` (`email`);

-- -----------------------------------------------------
-- Table `user_contacts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `user_contacts`;

CREATE TABLE IF NOT EXISTS `user_contacts`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      VARCHAR(36)  NOT NULL REFERENCES users (`id`) ON DELETE CASCADE,
    `contact_id`   BIGINT       NOT NULL REFERENCES contacts (`id`) ON DELETE CASCADE,
    `contact_name` VARCHAR(255) NOT NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_user_contact` ON `user_contacts` (`user_id`, `contact_id`);

-- -----------------------------------------------------
-- Table `user_devices`
-- -----------------------------------------------------

CREATE TABLE `user_devices`
(
    `id`           BIGINT                     NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      VARCHAR(36)                NOT NULL REFERENCES users (`id`) ON DELETE CASCADE,
    `device_type`  ENUM ('MOBILE', 'DESKTOP') NOT NULL,
    `device_token` VARCHAR(255)               NOT NULL,
    `created_at`   DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX `idx_user_device_type` ON `user_devices` (`user_id`, `device_type`);

ALTER TABLE `user_devices`
    ADD CONSTRAINT uc_device_token UNIQUE (device_token);

SET FOREIGN_KEY_CHECKS = 1;

--changeset tranhieu956230@gmail.com:2

-- -----------------------------------------------------
-- Table `conversations`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `conversations`;

CREATE TABLE IF NOT EXISTS `conversations`
(
    `id`                BIGINT                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `title`             VARCHAR(255)            NOT NULL,
    `creator_id`        VARCHAR(36)             NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    `type`              ENUM('SINGLE', 'GROUP') NOT NULL,
    `created_at`        DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `messages`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `messages`;

CREATE TABLE IF NOT EXISTS `messages`
(
    `id`                BIGINT                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `message`           TEXT                    NOT NULL,
    `sender_id`         VARCHAR(36)             NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    `conversation_id`   BIGINT                  NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    `type`              ENUM('PLAINTEXT', 'STICKER', 'GIF', 'PHOTO', 'DOCUMENT') NOT NULL,
    `created_at`        DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `conversations_participants`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `conversations_participants`;

CREATE TABLE IF NOT EXISTS `conversations_participants`
(
    `id`                BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `participant_id`    VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    `conversation_id`   BIGINT      NOT NULL REFERENCES conversations(id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_conversation_participant` ON `conversations_participants` (`conversation_id`, `participant_id`);