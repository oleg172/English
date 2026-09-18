--liquibase formatted sql

--changeSet omiliaev:07-add-topic-table logicalFilePath:sql/07-add-topic-table

CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_topics_user
        FOREIGN KEY (user_id)
        REFERENCES telegram_users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_topics_user_name UNIQUE (user_id, name)
);

CREATE INDEX idx_topics_user_id ON topics(user_id);