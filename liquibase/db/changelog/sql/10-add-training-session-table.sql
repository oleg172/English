--liquibase formatted sql

--changeSet omiliaev:10-add-training-session-table logicalFilePath:sql/10-add-training-session-table

CREATE TABLE training_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    started_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(20) NOT NULL,
    mode VARCHAR(20) NOT NULL,
    total_words INTEGER NOT NULL,
    answered_words INTEGER NOT NULL DEFAULT 0,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    incorrect_answers INTEGER NOT NULL DEFAULT 0,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_training_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES telegram_users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_training_sessions_user_id
    ON training_sessions(user_id);

CREATE INDEX idx_training_sessions_status
    ON training_sessions(status);