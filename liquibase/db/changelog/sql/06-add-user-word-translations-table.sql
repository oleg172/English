--liquibase formatted sql

--changeSet omiliaev:06-add-user-word-translations-table logicalFilePath:sql/06-add-user-word-translations-table

CREATE TABLE user_word_translations (
    id BIGSERIAL PRIMARY KEY,
    user_word_id BIGINT NOT NULL,
    translation VARCHAR(1000) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_word_translations_user_word
        FOREIGN KEY (user_word_id)
        REFERENCES user_words(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_word_translations UNIQUE (user_word_id, translation)
);

CREATE INDEX idx_user_word_translations_user_word_id ON user_word_translations(user_word_id);