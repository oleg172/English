--liquibase formatted sql

--changeSet omiliaev:08-add-user-word-topic-table logicalFilePath:sql/08-add-user-word-topic-table

CREATE TABLE user_word_topics (
    id BIGSERIAL PRIMARY KEY,
    user_word_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_word_topics_user_word
        FOREIGN KEY (user_word_id)
        REFERENCES user_words(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_word_topics_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_word_topics UNIQUE (user_word_id, topic_id)
);

CREATE INDEX idx_user_word_topics_user_word_id
    ON user_word_topics(user_word_id);

CREATE INDEX idx_user_word_topics_topic_id
    ON user_word_topics(topic_id);