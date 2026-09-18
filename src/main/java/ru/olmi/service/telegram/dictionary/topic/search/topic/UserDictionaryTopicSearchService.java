package ru.olmi.service.telegram.dictionary.topic.search.topic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.service.storage.TopicStorage;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicSearchService {
    private final TopicStorage topicStorage;
    private final UserDictionaryTopicSearchState state;
    private final UserDictionaryTopicSearchView view;

    public SendMessage start(TelegramUser user, Long chatId) {
        state.start(user);

        return view.enterQuery(chatId);
    }

    public SendMessage search(TelegramUser user, Long chatId, String query) {
        query = query.trim();

        if (query.isEmpty()) {
            return view.enterQuery(chatId);
        }

        state.setQuery(user, query);

        return page(user, chatId, 0);
    }

    public SendMessage next(TelegramUser user, Long chatId) {
        return page(user, chatId, state.getPage(user) + 1);
    }

    public SendMessage previous(TelegramUser user, Long chatId) {
        return page(user, chatId, state.getPage(user) - 1);
    }

    public SendMessage current(TelegramUser user, Long chatId) {
        return page(user, chatId, state.getPage(user));
    }

    private SendMessage page(TelegramUser user, Long chatId, int page) {
        if (page < 0) {
            page = 0;
        }

        Page<Topic> result = topicStorage.searchTopicsForUser(user, state.getQuery(user), page);

        if (result.isEmpty() && page > 0) {
            return page(user, chatId, page - 1);
        }

        if (result.isEmpty()) {
            return view.empty(chatId, state.getQuery(user));
        }

        state.setPage(user, result.getNumber());

        return view.results(chatId, state.getQuery(user), result);
    }
}
