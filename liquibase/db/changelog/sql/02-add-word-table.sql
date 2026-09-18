--liquibase formatted sql

--changeSet omiliaev:02-add-word-table logicalFilePath:sql/02-add-word-table

CREATE TABLE words (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    from_language VARCHAR(10) NOT NULL,
    to_language VARCHAR(10) NOT NULL,
    transcription VARCHAR(1000),
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_words_word_languages UNIQUE (word, from_language, to_language)
);

CREATE INDEX idx_words_word ON words(word);

CREATE INDEX idx_words_languages ON words(from_language, to_language);