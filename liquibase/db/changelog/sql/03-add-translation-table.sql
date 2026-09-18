--liquibase formatted sql

--changeSet omiliaev:03-add-translation-table logicalFilePath:sql/03-add-translation-table

CREATE TABLE translations (
    id BIGSERIAL PRIMARY KEY,
    word_id BIGINT NOT NULL,
    translation VARCHAR(1000) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_translations_word
        FOREIGN KEY (word_id)
        REFERENCES words(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_translation_word_translation
        UNIQUE (word_id, translation)
);

CREATE INDEX idx_translations_word_id
    ON translations(word_id);