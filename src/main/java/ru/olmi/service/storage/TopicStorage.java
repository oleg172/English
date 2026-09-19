package ru.olmi.service.storage;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTopic;
import ru.olmi.repository.TopicRepository;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.repository.UserWordTopicRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class TopicStorage {

    private final TopicRepository topicRepository;
    private final UserWordTopicRepository userWordTopicRepository;
    private final UserWordRepository userWordRepository;

    public boolean addTopic(TelegramUser user, Long userWordId, String topicName) {
        UserWord userWord = userWordRepository
                .findByIdAndUserId(userWordId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User word not found: " + userWordId));

        Topic topic = topicRepository.findByUserIdAndName(user.getId(), topicName)
                                     .orElseGet(() ->
                                             topicRepository.save(new Topic()
                                                     .setUser(user)
                                                     .setName(topicName)
                                             ));

        boolean alreadyExists = userWord.getTopics().stream()
                                        .anyMatch(value -> value.getTopic().getId().equals(topic.getId()));

        if (alreadyExists) {
            return false;
        }

        userWord.getTopics().add(new UserWordTopic()
                .setUserWord(userWord)
                .setTopic(topic));
        return true;
    }

    @Transactional(readOnly = true)
    public List<UserWordTopic> findTopicsForEdit(TelegramUser user, Long userWordId) {
        return userWordTopicRepository.findTopics(userWordId, user.getId());
    }

    public boolean deleteTopic(TelegramUser user, Long userWordId, Long userWordTopicId) {
        UserWordTopic userWordTopic = userWordTopicRepository.findByIdAndUserWordIdAndUserId(userWordTopicId, userWordId, user.getId())
                                                             .orElse(null);

        if (userWordTopic == null) {
            return false;
        }

        userWordTopicRepository.delete(userWordTopic);

        return true;
    }

    @Transactional(readOnly = true)
    public Page<Topic> findTopicsForUser(TelegramUser user, int page) {
        return userWordTopicRepository.findTopicsForUser(user.getId(), PageRequest.of(page, 10));
    }

    @Transactional(readOnly = true)
    public Page<UserWord> findWordsByTopic(TelegramUser user, Long topicId, int page) {
        return userWordTopicRepository.findWordsByTopic(user.getId(), topicId, PageRequest.of(page, 10));
    }

    @Transactional(readOnly = true)
    public Optional<Topic> findForUser(TelegramUser user, Long topicId) {
        return topicRepository.findByIdAndUserId(
                topicId,
                user.getId()
        );
    }

    @Transactional(readOnly = true)
    public Page<Topic> searchTopicsForUser(TelegramUser user, String query, int page) {
        return userWordTopicRepository.searchTopicsForUser(user.getId(), query, PageRequest.of(page, 10));
    }

    @Transactional(readOnly = true)
    public Page<UserWord> searchWordsByTopic(TelegramUser user, Long topicId, String query, int page) {
        return userWordTopicRepository.searchWordsByTopic(user.getId(), topicId, query, PageRequest.of(page, 10));
    }
}
