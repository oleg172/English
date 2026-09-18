package ru.olmi.service.telegram.dictionary.topic;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.telegram.dictionary.topic.state.UserDictionaryTopicDialogState;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicService {

    private final TopicStorage topicStorage;
    private final UserDictionaryTopicDialogState state;
    private final UserDictionaryTopicView view;

    public SendMessage topics(TelegramUser user, Long chatId) {
        state.start(user);

        return topicsPage(user, chatId, 0);
    }

    public SendMessage nextTopics(TelegramUser user, Long chatId) {
        return topicsPage(user, chatId, state.getTopicPage(user) + 1);
    }

    public SendMessage previousTopics(TelegramUser user, Long chatId) {
        return topicsPage(user, chatId, state.getTopicPage(user) - 1);
    }

    public SendMessage currentTopics(TelegramUser user, Long chatId) {
        return topicsPage(user, chatId, state.getTopicPage(user));
    }

    public SendMessage selectTopic(TelegramUser user, Long chatId, Long topicId) {
        if (topicStorage.findForUser(user, topicId).isEmpty()) {
            return view.emptyTopics(chatId);
        }

        state.setTopic(user, topicId);
        return wordsPage(user, chatId, 0);
    }

    public SendMessage nextWords(TelegramUser user, Long chatId) {
        return wordsPage(user, chatId, state.getWordPage(user) + 1);
    }

    public SendMessage previousWords(TelegramUser user, Long chatId) {
        return wordsPage(user, chatId, state.getWordPage(user) - 1);
    }

    public SendMessage currentWords(TelegramUser user, Long chatId) {
        return wordsPage(user, chatId, state.getWordPage(user));
    }

    private SendMessage topicsPage(TelegramUser user, Long chatId, int page) {
        if (page < 0) {
            page = 0;
        }

        Page<Topic> result = topicStorage.findTopicsForUser(user, page);

        if (result.isEmpty() && page > 0) {
            return topicsPage(user, chatId, page - 1);
        }

        if (result.isEmpty()) {
            return view.emptyTopics(chatId);
        }

        state.setTopicPage(user, result.getNumber());
        return view.topics(chatId, result);
    }

    private SendMessage wordsPage(TelegramUser user, Long chatId, int page) {
        Long topicId = state.getTopicId(user);
        if (topicId == null) {
            return view.emptyTopics(chatId);
        }

        Optional<Topic> topic = topicStorage.findForUser(user, topicId);

        if (topic.isEmpty()) {
            return view.emptyTopics(chatId);
        }

        if (page < 0) {
            page = 0;
        }

        Page<UserWord> result = topicStorage.findWordsByTopic(user, topicId, page);
        if (result.isEmpty() && page > 0) {
            return wordsPage(user, chatId, page - 1);
        }

        if (result.isEmpty()) {
            return view.emptyWords(chatId, topic.get());
        }

        state.setWordPage(user, result.getNumber());
        return view.words(chatId, topic.get(), result);
    }
}