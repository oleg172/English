--liquibase formatted sql

--changeSet omiliaev:05-add-user-words-table logicalFilePath:sql/05-add-user-words-table

CREATE TABLE user_words (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_words_user
        FOREIGN KEY (user_id)
        REFERENCES telegram_users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_words_word
        FOREIGN KEY (word_id)
        REFERENCES words(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_words_user_word UNIQUE (user_id, word_id)
);

CREATE INDEX idx_user_words_user_id ON user_words(user_id);

CREATE INDEX idx_user_words_word_id ON user_words(word_id);