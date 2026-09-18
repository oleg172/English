--liquibase formatted sql

--changeSet omiliaev:01-add-telegram-user-table logicalFilePath:sql/01-add-telegram-user-table
CREATE TABLE telegram_users (
    id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    language_code VARCHAR(10) NOT NULL,
    preferred_from_language VARCHAR(10) NOT NULL,
    preferred_to_language VARCHAR(10) NOT NULL,
    prefer_user_translation BOOLEAN NOT NULL DEFAULT FALSE,
    prefer_user_topics BOOLEAN NOT NULL DEFAULT FALSE,
    last_activity TIMESTAMP WITHOUT TIME ZONE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);