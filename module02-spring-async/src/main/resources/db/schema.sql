SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `notification_logs`;
DROP TABLE IF EXISTS `async_retry_jobs`;
DROP TABLE IF EXISTS `certificates`;
DROP TABLE IF EXISTS `enrollments`;
DROP TABLE IF EXISTS `courses`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `users` (
    `user_id` bigint NOT NULL AUTO_INCREMENT,
    `email` varchar(255) NOT NULL UNIQUE,
    `password_hash` varchar(255) NULL,
    `name` varchar(100) NOT NULL,
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
    `last_login_at` timestamp NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    CHECK (`status` IN ('ACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE TABLE `courses` (
    `course_id` bigint NOT NULL AUTO_INCREMENT,
    `author_id` bigint NOT NULL,
    `title` varchar(255) NOT NULL,
    `description` text NULL,
    `status` varchar(20) NOT NULL DEFAULT 'DRAFT',
    PRIMARY KEY (`course_id`),
    FOREIGN KEY (`author_id`) REFERENCES `users` (`user_id`),
    CHECK (`status` IN ('DRAFT', 'PUBLISHED'))
);

CREATE TABLE `enrollments` (
    `enrollment_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `course_id` bigint NOT NULL,
    `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
    `enrolled_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completed_at` timestamp NULL,
    PRIMARY KEY (`enrollment_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
    CHECK (`status` IN ('ACTIVE', 'COMPLETED'))
);

CREATE TABLE `certificates` (
    `certificate_id` bigint NOT NULL AUTO_INCREMENT,
    `enrollment_id` bigint NOT NULL UNIQUE,
    `verification_code` varchar(100) NOT NULL UNIQUE,
    `issued_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`certificate_id`),
    FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`enrollment_id`)
);

CREATE TABLE `notification_logs` (
    `notification_log_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `channel` varchar(30) NOT NULL,
    `message` varchar(255) NOT NULL,
    `sent_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`notification_log_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `async_retry_jobs` (
    `async_retry_job_id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `channel` varchar(30) NOT NULL,
    `message` varchar(255) NOT NULL,
    `status` varchar(20) NOT NULL,
    `retry_count` int NOT NULL,
    `max_retry_count` int NOT NULL,
    `fail_until_attempt` int NOT NULL,
    `next_retry_at` timestamp NOT NULL,
    `last_error_message` varchar(255) NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`async_retry_job_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);
