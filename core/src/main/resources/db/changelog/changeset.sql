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
    `image_url`         VARCHAR(255)            NOT NULL,
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

--changeset tranhieu956230@gmail.com:3

-- -----------------------------------------------------
-- Table `message_status`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `message_status`;

CREATE TABLE IF NOT EXISTS `message_status`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    `message_id`   BIGINT       NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    `status`       ENUM('SEEN') NOT NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_user_message_status` ON `message_status` (`user_id`, `message_id`, `status`);

--changeset nguyenbrother9x@gmail.com:4

-- -----------------------------------------------------
-- Table `sticker_packs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `sticker_packs`;

CREATE TABLE IF NOT EXISTS `sticker_packs`
(
    `id`                      BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`                    VARCHAR(36)  NOT NULL,
    `publisher`               VARCHAR(128) NOT NULL,
    `tray_image_file`         VARCHAR(128) NOT NULL,
    `is_animated`   TINYINT(1)   NOT NULL DEFAULT 0
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `stickers`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `stickers`;

CREATE TABLE IF NOT EXISTS `stickers`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `pack_id`      BIGINT       NOT NULL REFERENCES `sticker_packs` (`id`) ON DELETE CASCADE,
    `image_file`   VARCHAR(128) NOT NULL,
    `emojis`       VARCHAR(128) NOT NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_id_pack` ON `stickers` (`id`, `pack_id`);

--changeset tranhieu956230@gmail.com:5

-- -----------------------------------------------------
-- Table `attachments`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `attachments`;

CREATE TABLE IF NOT EXISTS `attachments`
(
    `id`            BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `message_id`    BIGINT       NOT NULL REFERENCES `messages`(`id`) ON DELETE CASCADE,
    `name`          VARCHAR(255) NOT NULL,
    `original_name` VARCHAR(255) NOT NULL,
    `size`          BIGINT       NOT NULL,
    `md5`           VARCHAR(255) NOT NULL,
    `type`          VARCHAR(255) NOT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE FULLTEXT INDEX idx_original_name ON attachments(original_name);

--changeset tranhieu956230@gmail.com:6

-- -----------------------------------------------------
-- Table `channels`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `channels`;

CREATE TABLE IF NOT EXISTS `channels`
(
    `id`            VARCHAR(255) NOT NULL PRIMARY KEY,
    `title`         VARCHAR(255) NOT NULL,
    `thumbnail`     VARCHAR(255) NOT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `videos`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `videos`;

CREATE TABLE IF NOT EXISTS `videos`
(
    `id`              VARCHAR(255) NOT NULL PRIMARY KEY,
    `title`           VARCHAR(255) NOT NULL,
    `thumbnail`       VARCHAR(255) NOT NULL,
    `url`             VARCHAR(255) NOT NULL,
    `published_at`    DATETIME     NOT NULL,
    `duration`        VARCHAR(255) NOT NULL,
    `duration_ms`     BIGINT       NOT NULL,
    `view_count`      BIGINT       NOT NULL,
    `like_count`      BIGINT       NOT NULL,
    `favorite_count`  BIGINT       NOT NULL,
    `comment_count`   BIGINT       NOT NULL,
    `is_highlighted`  TINYINT      NOT NULL,
    `channel_id`      VARCHAR(255) NOT NULL REFERENCES `messages`(`id`) ON DELETE CASCADE,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;