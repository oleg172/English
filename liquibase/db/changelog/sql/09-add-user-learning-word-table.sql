--liquibase formatted sql

--changeSet omiliaev:09-add-user-learning-word-table logicalFilePath:sql/09-add-user-learning-word-table

CREATE TABLE user_learning_words (
    id BIGSERIAL PRIMARY KEY,
    user_word_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    incorrect_answers INTEGER NOT NULL DEFAULT 0,
    consecutive_correct_answers INTEGER NOT NULL DEFAULT 0,
    interval_seconds BIGINT NOT NULL DEFAULT 0,
    ease_factor NUMERIC(4, 2) NOT NULL DEFAULT 2.50,
    last_reviewed_at TIMESTAMP WITHOUT TIME ZONE,
    next_review_at TIMESTAMP WITHOUT TIME ZONE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_learning_words_user_word
        FOREIGN KEY (user_word_id)
        REFERENCES user_words(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_learning_words_user_word
        UNIQUE (user_word_id)
);

CREATE INDEX idx_user_learning_words_status
    ON user_learning_words(status);

CREATE INDEX idx_user_learning_words_next_review_at
    ON user_learning_words(next_review_at);