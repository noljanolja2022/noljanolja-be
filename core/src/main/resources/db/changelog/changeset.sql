-- liquibase formatted sql

-- changeset itanchi.dev@gmail.com:1

SET NAMES utf8mb4;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `users`;

create TABLE IF NOT EXISTS `users`
(
    `id`            VARCHAR(36)                      NOT NULL PRIMARY KEY,
    `name`          VARCHAR(255)                     NOT NULL,
    `avatar`        TEXT                             NULL,
    `country_code`  VARCHAR(7)                       NOT NULL,
    `phone_number`  VARCHAR(12)                      NOT NULL,
    `email`         VARCHAR(255)                     NULL,
    `dob`           DATE                             NULL,
    `gender`        ENUM ('MALE', 'FEMALE', 'OTHER') NULL,
    `is_active`     TINYINT(1)                       NOT NULL DEFAULT 1,
    `is_reported`   TINYINT(1)                       NOT NULL DEFAULT 0,
    `is_blocked`    TINYINT(1)                       NOT NULL DEFAULT 0,
    `is_deleted`    TINYINT(1)                       NOT NULL DEFAULT 0,
    `preferences`   TEXT                             NOT NULL,
    `referral_code` VARCHAR(8)                       NOT NULL,
    `referred_by`   VARCHAR(255)                     NOT NULL,
    `created_at`    DATETIME                         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME                         NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FULLTEXT idx_referral_code (`referral_code`)
) ENGINE = InnoDB;

create INDEX `idx_phone_number` ON `users` (`phone_number`);

create UNIQUE INDEX `idx_email` ON `users` (`email`);

-- -----------------------------------------------------
-- Table `contacts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `contacts`;

create TABLE IF NOT EXISTS `contacts`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `country_code` VARCHAR(7)   NULL,
    `phone_number` VARCHAR(12)  NULL,
    `email`        VARCHAR(255) NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

create UNIQUE INDEX `idx_country_code_phone_number` ON `contacts` (`country_code`, `phone_number`);

create INDEX `idx_phone_number` ON `contacts` (`phone_number`);

create UNIQUE INDEX `idx_email` ON `contacts` (`email`);

-- -----------------------------------------------------
-- Table `user_contacts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `user_contacts`;

create TABLE IF NOT EXISTS `user_contacts`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      VARCHAR(36)  NOT NULL,
    `contact_id`   BIGINT       NOT NULL,
    `contact_name` VARCHAR(255) NOT NULL,
    `is_blocked`   TINYINT      NOT NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON delete CASCADE,
    FOREIGN KEY (contact_id) REFERENCES contacts (id) ON delete CASCADE
) ENGINE = InnoDB;

create UNIQUE INDEX `idx_user_contact` ON `user_contacts` (`user_id`, `contact_id`);

-- -----------------------------------------------------
-- Table `user_devices`
-- -----------------------------------------------------

create TABLE `user_devices`
(
    `id`           BIGINT                     NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      VARCHAR(36)                NOT NULL,
    `device_type`  ENUM ('MOBILE', 'DESKTOP') NOT NULL,
    `device_token` VARCHAR(255)               NOT NULL,
    `created_at`   DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON delete CASCADE
);

create UNIQUE INDEX `idx_user_device_type` ON `user_devices` (`user_id`, `device_type`);

ALTER TABLE `user_devices`
    ADD CONSTRAINT uc_device_token UNIQUE (device_token);

SET FOREIGN_KEY_CHECKS = 1;

-- changeset tranhieu956230@gmail.com:2

-- -----------------------------------------------------
-- Table `conversations`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `conversations`;

create TABLE IF NOT EXISTS `conversations`
(
    `id`                BIGINT                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `title`             VARCHAR(255)            NOT NULL,
    `creator_id`        VARCHAR(36)             NOT NULL,
    `type`              ENUM('SINGLE', 'GROUP') NOT NULL,
    `image_url`         VARCHAR(255)            NOT NULL,
    `created_at`        DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `admin_id`          VARCHAR(36)             NOT NULL DEFAULT '',
    FOREIGN KEY (creator_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (admin_id) REFERENCES users(id) ON delete CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `messages`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `messages`;

create TABLE IF NOT EXISTS `messages`
(
    `id`                    BIGINT                  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `message`               TEXT                    NOT NULL,
    `sender_id`             VARCHAR(36)             NOT NULL,
    `conversation_id`       BIGINT                  NOT NULL,
    `type`                  ENUM('PLAINTEXT', 'STICKER', 'VOICE', 'PHOTO', 'FILE', 'EVENT_UPDATED', 'EVENT_LEFT', 'EVENT_JOINED') NOT NULL,
    `created_at`            DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_deleted`            TINYINT                 NOT NULL,
    `reply_to_message_id`   BIGINT                  NULL,
    `share_message_id`      BIGINT                  NULL,
    `share_video_id`        VARCHAR(255)            NULL,
    `left_participant_ids`  VARCHAR(255)            NULL,
    `join_participant_ids`  VARCHAR(255)            NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON delete CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `conversations_participants`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `conversations_participants`;

create TABLE IF NOT EXISTS `conversations_participants`
(
    `id`                BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `participant_id`    VARCHAR(36) NOT NULL,
    `conversation_id`   BIGINT      NOT NULL,

    FOREIGN KEY (participant_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON delete CASCADE
) ENGINE = InnoDB;

create UNIQUE INDEX `idx_conversation_participant` ON `conversations_participants` (`conversation_id`, `participant_id`);

-- changeset tranhieu956230@gmail.com:3

-- -----------------------------------------------------
-- Table `message_status`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `message_status`;

create TABLE IF NOT EXISTS `message_status`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      VARCHAR(36)  NOT NULL,
    `message_id`   BIGINT       NOT NULL,
    `status`       ENUM('SEEN','REMOVED') NOT NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON delete CASCADE
) ENGINE = InnoDB;

create UNIQUE INDEX `idx_user_message_status` ON `message_status` (`user_id`, `message_id`, `status`);

-- changeset nguyenbrother9x@gmail.com:4

-- -----------------------------------------------------
-- Table `sticker_packs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `sticker_packs`;

create TABLE IF NOT EXISTS `sticker_packs`
(
    `id`                      BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`                    VARCHAR(36)  NOT NULL,
    `publisher`               VARCHAR(128) NOT NULL,
    `tray_image_file`         VARCHAR(128) NOT NULL,
    `is_active`               TINYINT(1)   NOT NULL DEFAULT 1,
    `is_animated`             TINYINT(1)   NOT NULL DEFAULT 0
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `stickers`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `stickers`;

create TABLE IF NOT EXISTS `stickers`
(
    `id`           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `pack_id`      BIGINT       NOT NULL,
    `image_file`   VARCHAR(128) NOT NULL,
    `emojis`       VARCHAR(128) NOT NULL,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (pack_id) REFERENCES sticker_packs(id) ON delete CASCADE
) ENGINE = InnoDB;

create UNIQUE INDEX `idx_id_pack` ON `stickers` (`id`, `pack_id`);

-- changeset tranhieu956230@gmail.com:5

-- -----------------------------------------------------
-- Table `attachments`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `attachments`;

create TABLE IF NOT EXISTS `attachments`
(
    `id`                BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `message_id`        BIGINT       NOT NULL,
    `name`              VARCHAR(255) NOT NULL,
    `original_name`     VARCHAR(255) NOT NULL,
    `size`              BIGINT       NOT NULL,
    `md5`               VARCHAR(255) NOT NULL,
    `type`              VARCHAR(255) NOT NULL,
    `attachment_type`   ENUM('PHOTO','VIDEO','INTERNAL_VIDEO','LINK','FILE') NOT NULL DEFAULT('PHOTO'),
    `preview_image`     VARCHAR(255) NOT NULL,
    `duration_ms`       BIGINT       NOT NULL,
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (message_id) REFERENCES messages(id) ON delete CASCADE
) ENGINE = InnoDB;

create FULLTEXT INDEX idx_original_name ON attachments(original_name);

-- changeset tranhieu956230@gmail.com:6

-- -----------------------------------------------------
-- Table `video_channels`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_channels`;

create TABLE IF NOT EXISTS `video_channels`
(
    `id`            VARCHAR(255) NOT NULL PRIMARY KEY,
    `title`         VARCHAR(255) NOT NULL,
    `thumbnail`     VARCHAR(255) NOT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_categories`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_categories`;

create TABLE IF NOT EXISTS `video_categories`
(
    `id`            VARCHAR(255) NOT NULL PRIMARY KEY,
    `title`         VARCHAR(255) NOT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `videos`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `videos`;

create TABLE IF NOT EXISTS `videos`
(
    `id`              VARCHAR(255) NOT NULL PRIMARY KEY,
    `title`           VARCHAR(255) NOT NULL,
    `thumbnail`       VARCHAR(255) NOT NULL,
    `url`             VARCHAR(255) NOT NULL,
    `published_at`    DATETIME     NOT NULL,
    `duration`        VARCHAR(255) NOT NULL,
    `duration_ms`     BIGINT       NOT NULL,
    `favorite_count`  BIGINT       NOT NULL,
    `view_count`      BIGINT       NOT NULL DEFAULT 0,
    `like_count`      BIGINT       NOT NULL DEFAULT 0,
    `comment_count`   BIGINT       NOT NULL DEFAULT 0,
    `is_highlighted`  TINYINT      NOT NULL,
    `channel_id`      VARCHAR(255) NOT NULL,
    `category_id`     VARCHAR(255) NOT NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (channel_id) REFERENCES video_channels(id) ON delete CASCADE,
    FOREIGN KEY (category_id) REFERENCES video_categories(id) ON delete CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_view_counts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_view_counts`;

create TABLE IF NOT EXISTS `video_view_counts`
(
    `id`              BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `video_id`        VARCHAR(255) NOT NULL,
    `view_count`      BIGINT       NOT NULL,
    `created_at`      DATE         NOT NULL DEFAULT (CURRENT_DATE),

    FOREIGN KEY (video_id) REFERENCES videos(id) ON delete CASCADE
) ENGINE = InnoDB;

create INDEX idx_video_view_counts_created_at ON video_view_counts(created_at);

-- -----------------------------------------------------
-- Table `video_users`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_users`;

create TABLE IF NOT EXISTS `video_users`
(
    `id`              BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `video_id`        VARCHAR(255) NOT NULL,
    `user_id`         VARCHAR(255) NOT NULL,
    `is_liked`        TINYINT      NOT NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_ignored`      TINYINT(1)   NOT NULL DEFAULT 0,

    FOREIGN KEY (video_id) REFERENCES videos(id) ON delete CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_comments`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_comments`;

create TABLE IF NOT EXISTS `video_comments`
(
    `id`              BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `video_id`        VARCHAR(255) NOT NULL,
    `commenter_id`    VARCHAR(255) NOT NULL,
    `comment`         TEXT         NOT NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (video_id) REFERENCES videos(id) ON delete CASCADE,
    FOREIGN KEY (commenter_id) REFERENCES users(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset nguyenbrother9x@gmail.com:7

-- -----------------------------------------------------
-- Table `member_info`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `member_info`;

create TABLE IF NOT EXISTS `member_info`
(
    `id`                 VARCHAR(36)      NOT NULL PRIMARY KEY,
    `accumulated_points` BIGINT           NOT NULL DEFAULT 0,
    `available_points`   BIGINT           NOT NULL DEFAULT 0
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:8

-- -----------------------------------------------------
-- Table `tier_configs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `tier_configs`;

create TABLE IF NOT EXISTS `tier_configs`
(
    `id`              BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `tier`            ENUM ('BRONZE', 'SILVER', 'GOLD', 'DIAMOND') NOT NULL DEFAULT 'BRONZE',
    `min_point`       BIGINT       NOT NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `transactions`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `transactions`;

create TABLE IF NOT EXISTS `transactions`
(
    `id`              BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`       VARCHAR(36)  NOT NULL,
    `amount`          BIGINT       NOT NULL,
    `reason`          VARCHAR(255) NOT NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `log`             TEXT         NULL,

    FOREIGN KEY (member_id) REFERENCES member_info(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:9

-- -----------------------------------------------------
-- Table `chat_reward_configs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `chat_reward_configs`;

create TABLE IF NOT EXISTS `chat_reward_configs`
(
    `id`                    BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `number_of_messages`    INT         NOT NULL,
    `reward_point`          BIGINT      NOT NULL,
    `max_apply_times`       INT         NOT NULL,
    `active`                TINYINT     NOT NULL,
    `only_reward_creator`   TINYINT     NOT NULL,
    `room_type`             ENUM('SINGLE', 'GROUP') NOT NULL,
    `created_at`            DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_reward_configs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_reward_configs`;

create TABLE IF NOT EXISTS `video_reward_configs`
(
    `id`                            BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `video_id`                      VARCHAR(255)    NOT NULL,
    `active`                        TINYINT         NOT NULL,
    `max_apply_times`               INT             NOT NULL,
    `total_points`                  BIGINT          NULL,
    `rewarded_points`               BIGINT          NOT NULL,
    `comment_max_apply_times`       INT             NOT NULL,
    `min_comment_length`            INT             NOT NULL,
    `comment_reward_points`         BIGINT          NOT NULL,
    `comment_total_applied_times`   INT             NOT NULL,
    `like_total_applied_times`      INT             NOT NULL,
    `like_max_apply_times`          INT             NOT NULL,
    `like_reward_points`            BIGINT          NOT NULL,
    `created_at`                    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`                    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_reward_progresses_configs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_reward_progresses_configs`;

create TABLE IF NOT EXISTS `video_reward_progresses_configs`
(
    `id`            BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `config_id`     BIGINT      NOT NULL,
    `progress`      DOUBLE      NOT NULL,
    `reward_point`  BIGINT      NOT NULL,
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (config_id) REFERENCES video_reward_configs(id) ON delete CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_reward_records`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_reward_records`;

create TABLE IF NOT EXISTS `video_reward_records`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`           VARCHAR(255)    NOT NULL,
    `config_id`         BIGINT          NOT NULL,
    `reward_progress`   DOUBLE          NOT NULL,
    `video_id`          VARCHAR(255)    NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:10

-- -----------------------------------------------------
-- Table `chat_reward_records`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `chat_reward_records`;

create TABLE IF NOT EXISTS `chat_reward_records`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`           VARCHAR(255)    NOT NULL,
    `conversation_id`   VARCHAR(255)    NOT NULL,
    `apply_times`       INT             NOT NULL,
    `message_count`     INT             NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:11

-- -----------------------------------------------------
-- Table `gift_brands`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `gift_brands`;

create TABLE IF NOT EXISTS `gift_brands`
(
    `id`                VARCHAR(255)    NOT NULL PRIMARY KEY,
    `name`              VARCHAR(255)    NOT NULL,
    `image`             VARCHAR(255)    NOT NULL,
    `locale`            VARCHAR(255)    NULL
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `gift_categories`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `gift_categories`;

create TABLE IF NOT EXISTS `gift_categories`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`              VARCHAR(255)    NULL,
    `image`             VARCHAR(255)    NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `locale`            VARCHAR(255)    NULL

) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `gifts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `gifts`;

create TABLE IF NOT EXISTS `gifts`
(
    `id`                VARCHAR(64)     NOT NULL PRIMARY KEY,
    `gift_no`           BIGINT          NOT NULL,
    `name`              VARCHAR(255)    NOT NULL,
    `description`       TEXT            NOT NULL,
    `image`             VARCHAR(255)    NOT NULL,
    `end_time`          DATETIME        NOT NULL,
    `limit_day`         INT             NOT NULL DEFAULT 1,
    `brand_id`          VARCHAR(255)    NOT NULL,
    `category_id`       BIGINT          NULL,
    `price`             BIGINT          NOT NULL,
    `retail_price`      BIGINT          NOT NULL DEFAULT 0,
    `is_active`         TINYINT         NOT NULL DEFAULT 1,
    `is_featured`       TINYINT         NOT NULL DEFAULT 0,
    `is_today_offer`    TINYINT(1)      NOT NULL DEFAULT 0,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `locale`            VARCHAR(255)    NULL,

    FOREIGN KEY (brand_id) REFERENCES gift_brands(id) ON delete CASCADE
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `gift_transactions`;

create TABLE IF NOT EXISTS `gift_transactions`
(
    `id`                VARCHAR(20)     NOT NULL PRIMARY KEY,
    `gift_id`           VARCHAR(255)    NOT NULL ,
    `user_id`           VARCHAR(255)    NOT NULL ,
    `price`             BIGINT          NOT NULL,
    `order_no`          VARCHAR(64)     NOT NULL,
    `pin_no`            VARCHAR(64)     NULL,
    `bar_code`          VARCHAR(128)    NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (gift_id) REFERENCES gifts(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:12

-- -----------------------------------------------------
-- Table `message_reactions`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `message_reactions`;

create TABLE IF NOT EXISTS `message_reactions`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `code`              VARCHAR(50)     NOT NULL,
    `description`       VARCHAR(255)    NOT NULL,
    `code_inactive`     VARCHAR(50)     NOT NULL,
    `is_default`        TINYINT         NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `messages_participants_reactions`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `messages_participants_reactions`;

create TABLE IF NOT EXISTS `messages_participants_reactions`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `participant_id`    VARCHAR(255)    NOT NULL,
    `message_id`        BIGINT          NOT NULL,
    `reaction_id`       BIGINT          NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (participant_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON delete CASCADE,
    FOREIGN KEY (reaction_id) REFERENCES message_reactions(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:13

-- -----------------------------------------------------
-- Table `banners`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `banners`;

create TABLE IF NOT EXISTS `banners`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `title`             VARCHAR(255)    NOT NULL,
    `description`       TEXT            NOT NULL,
    `content`           TEXT            NOT NULL,
    `image`             TEXT            NOT NULL,
    `is_active`         TINYINT         NOT NULL DEFAULT 1,
    `priority`          ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT')     NOT NULL DEFAULT 'LOW',
    `action`            ENUM('NONE', 'LINK', 'SHARE', 'CHECKIN')    NOT NULL DEFAULT 'NONE',
    `action_url`        TEXT            NULL,
    `start_time`        DATETIME        NOT NULL,
    `end_time`          DATETIME        NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:14

-- -----------------------------------------------------
-- Table `video_comment_reward_records`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_comment_reward_records`;

create TABLE IF NOT EXISTS `video_comment_reward_records`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`           VARCHAR(255)    NOT NULL,
    `config_id`         BIGINT          NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (config_id) REFERENCES video_reward_configs(id) ON delete CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `video_like_reward_records`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `video_like_reward_records`;

create TABLE IF NOT EXISTS `video_like_reward_records`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`           VARCHAR(255)    NOT NULL,
    `config_id`         BIGINT          NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (config_id) REFERENCES video_reward_configs(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:15

-- -----------------------------------------------------
-- Table `checkin_reward_configs`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `checkin_reward_configs`;

create TABLE IF NOT EXISTS `checkin_reward_configs`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `day`               INT             NOT NULL,
    `reward_points`     BIGINT          NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `user_checkin_records`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `user_checkin_records`;

create TABLE IF NOT EXISTS `user_checkin_records`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `day`               INT             NOT NULL,
    `user_id`           VARCHAR(255)    NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com:16

DROP TABLE IF EXISTS `referral_reward_configs`;

create TABLE IF NOT EXISTS `referral_reward_configs`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `reward_points`     BIGINT          NOT NULL,
    `referee_points`    BIGINT          NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com::17

DROP TABLE IF EXISTS `promoted_videos`;

create TABLE IF NOT EXISTS `promoted_videos`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `video_id`          VARCHAR(255)    NOT NULL,
    `start_date`        DATE            NOT NULL DEFAULT (CURRENT_DATE),
    `end_date`          DATE            NOT NULL DEFAULT (CURRENT_DATE),
    `auto_play`         TINYINT(1)      NOT NULL DEFAULT 1,
    `auto_like`         TINYINT(1)      NOT NULL DEFAULT 0,
    `auto_subscribe`    TINYINT(1)      NOT NULL DEFAULT 0,
    `auto_comment`      TINYINT(1)      NOT NULL DEFAULT 0,
    `interaction_delay` INT             NOT NULL DEFAULT 0,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (video_id) REFERENCES videos(id) ON delete CASCADE
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `channel_subscription_records`;

create TABLE IF NOT EXISTS `channel_subscription_records`
(
    `id`                BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `channel_id`        VARCHAR(255)    NOT NULL,
    `user_id`           VARCHAR(255)    NOT NULL,
    `subscription_id`   VARCHAR(255)    NOT NULL,
    `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (channel_id) REFERENCES video_channels(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com::18

DROP TABLE IF EXISTS `user_balances`;

create TABLE IF NOT EXISTS `user_balances`
(
    `id`            BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`       VARCHAR(255)    NOT NULL,
    `balance`       BIGINT          NOT NULL DEFAULT 0,
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `coin_transactions`;

create TABLE IF NOT EXISTS `coin_transactions`
(
    `id`            BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `balance_id`    BIGINT          NOT NULL,
    `amount`        BIGINT          NOT NULL,
    `reason`        VARCHAR(255)    NOT NULL,
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `log`           TEXT            NULL,

    FOREIGN KEY (balance_id) REFERENCES user_balances(id) ON delete CASCADE
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `exchange_rate`;

create TABLE IF NOT EXISTS `exchange_rate`
(
    `id`                        BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `coin`                      INT         NOT NULL DEFAULT 0,
    `point`                     INT         NOT NULL DEFAULT 0,
    `reward_recurring_amount`   INT         NOT NULL DEFAULT 0,
    `created_at`                DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`                DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `promoted_video_user_logs`;

create TABLE IF NOT EXISTS `promoted_video_user_logs`
(
    `id`            BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`       VARCHAR(36)     NOT NULL,
    `video_id`      VARCHAR(255)    NOT NULL,
    `channel_id`    VARCHAR(255)    NOT NULL,
    `liked`         TINYINT(1)      NOT NULL DEFAULT 0,
    `commented`     TINYINT(1)      NOT NULL DEFAULT 0,
    `subscribed`    TINYINT(1)      NOT NULL DEFAULT 0,
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON delete CASCADE,
    FOREIGN KEY (video_id) REFERENCES videos(id) ON delete CASCADE,
    FOREIGN KEY (channel_id) REFERENCES video_channels(id) ON delete CASCADE
) ENGINE = InnoDB;

-- changeset tranhieu956230@gmail.com::19

DROP TABLE IF EXISTS `video_generated_comments`;

create TABLE IF NOT EXISTS `video_generated_comments`
(
    `id`            BIGINT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `content`       TEXT            NOT NULL,
    `video_id`      VARCHAR(255)    NOT NULL,
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,

    FOREIGN KEY (video_id) REFERENCES videos(id) ON delete CASCADE
) ENGINE = InnoDB;