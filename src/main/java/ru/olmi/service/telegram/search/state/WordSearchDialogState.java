package ru.olmi.service.telegram.search.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DialogState;
import ru.olmi.service.telegram.state.DialogStateStorage;

@Component
@RequiredArgsConstructor
public class WordSearchDialogState {

    private final DialogStateStorage stateStorage;

    private final Map<Long, SearchContext> contexts = new ConcurrentHashMap<>();

    public void start(TelegramUser user) {
        stateStorage.set(user.getId(), DialogState.WAITING_FOR_WORD_SEARCH);
    }

    public boolean isWaitingForQuery(TelegramUser user) {
        return stateStorage.get(user.getId()) == DialogState.WAITING_FOR_WORD_SEARCH;
    }

    public void setQuery(TelegramUser user, String query) {
        contexts.put(user.getId(), new SearchContext(query, 0));
    }

    public SearchContext getContext(TelegramUser user) {
        return contexts.get(user.getId());
    }

    public void setPage(TelegramUser user, int page) {
        SearchContext context = contexts.get(user.getId());

        if (context == null) {
            return;
        }

        contexts.put(user.getId(), new SearchContext(context.query(), page));
    }

    public void finish(TelegramUser user) {
        stateStorage.clear(user.getId());
        contexts.remove(user.getId());
    }

    /**
     * Хранит маску поиска и страницу
     * */
    public record SearchContext(String query, int page) {
    }
}
