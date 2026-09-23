--liquibase formatted sql

--changeSet omiliaev:11-add-training-session-word-table logicalFilePath:sql/11-add-training-session-word-table

CREATE TABLE training_session_words (
    id BIGSERIAL PRIMARY KEY,
    training_session_id BIGINT NOT NULL,
    user_learning_word_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    answered BOOLEAN NOT NULL DEFAULT FALSE,
    selected_answer VARCHAR(1000),
    correct_answer VARCHAR(1000) NOT NULL,
    answers TEXT NOT NULL,
    answered_at TIMESTAMP WITHOUT TIME ZONE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_training_session_words_session
        FOREIGN KEY (training_session_id)
        REFERENCES training_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_training_session_words_learning_word
        FOREIGN KEY (user_learning_word_id)
        REFERENCES user_learning_words(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_training_session_words_session_word
        UNIQUE (training_session_id, user_learning_word_id),

    CONSTRAINT uq_training_session_words_session_position
        UNIQUE (training_session_id, position)
);

CREATE INDEX idx_training_session_words_session_id
    ON training_session_words(training_session_id);

CREATE INDEX idx_training_session_words_learning_word_id
    ON training_session_words(user_learning_word_id);