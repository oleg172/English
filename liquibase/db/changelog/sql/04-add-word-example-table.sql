--liquibase formatted sql

--changeSet omiliaev:04-add-word-example-table logicalFilePath:sql/04-add-word-example-table

CREATE TABLE word_examples (
    id BIGSERIAL PRIMARY KEY,
    word_id BIGINT NOT NULL,
    example TEXT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_word_examples_word
        FOREIGN KEY (word_id)
        REFERENCES words(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_word_examples_word_id ON word_examples(word_id);