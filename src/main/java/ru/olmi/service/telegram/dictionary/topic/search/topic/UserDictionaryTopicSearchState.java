package ru.olmi.service.telegram.dictionary.topic.search.topic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;

@Component
public class UserDictionaryTopicSearchState {

    private final Map<Long, SearchContext> contexts = new ConcurrentHashMap<>();

    public void start(TelegramUser user) {
        contexts.put(user.getId(), new SearchContext("", 0));
    }

    public String getQuery(TelegramUser user) {
        return getContext(user).query();
    }

    public int getPage(TelegramUser user) {
        return getContext(user).page();
    }

    public void setQuery(TelegramUser user, String query) {
        contexts.put(user.getId(), new SearchContext(query, 0));
    }

    public void setPage(TelegramUser user, int page) {
        SearchContext context = getContext(user);

        contexts.put(user.getId(), new SearchContext(context.query(), page));
    }

    public void finish(TelegramUser user) {
        contexts.remove(user.getId());
    }

    private SearchContext getContext(TelegramUser user) {
        return contexts.getOrDefault(user.getId(), new SearchContext("", 0));
    }

    public boolean isWaitingForQuery(TelegramUser user) {
        return contexts.containsKey(user.getId()) && getQuery(user).isEmpty();
    }

    private record SearchContext(
            String query,
            int page
    ) {
    }
}
