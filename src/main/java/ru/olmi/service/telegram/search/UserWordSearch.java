package ru.olmi.service.telegram.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWord;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.search.state.WordSearchDialogState;

@Component
@RequiredArgsConstructor
public class UserWordSearch {

    private final UserWordStorage userWordStorage;
    private final WordSearchDialogState searchState;
    private final UserWordSearchView view;

    public SendMessage search(TelegramUser user, Long chatId, String query) {
        searchState.setQuery(user, query);
        return page(user, chatId, 0);
    }

    public SendMessage current(TelegramUser user, Long chatId) {
        WordSearchDialogState.SearchContext context = searchState.getContext(user);

        if (context == null) {
            return view.empty(chatId);
        }

        Page<UserWord> result = userWordStorage.search(user, context.query(), context.page());

        if (!result.isEmpty()) {
            return view.page(chatId, context.query(), result);
        }

        if (context.page() > 0) {
            return page(user, chatId, context.page() - 1);
        }

        searchState.finish(user);
        return view.empty(chatId);
    }

    public SendMessage next(TelegramUser user, Long chatId) {
        WordSearchDialogState.SearchContext context = searchState.getContext(user);

        if (context == null) {
            return view.empty(chatId);
        }

        return page(user, chatId, context.page() + 1);
    }

    public SendMessage previous(TelegramUser user, Long chatId) {
        WordSearchDialogState.SearchContext context = searchState.getContext(user);

        if (context == null) {
            return view.empty(chatId);
        }

        return page(user, chatId, context.page() - 1);
    }

    private SendMessage page(TelegramUser user, Long chatId, int page) {
        WordSearchDialogState.SearchContext context = searchState.getContext(user);

        if (context == null) {
            searchState.finish(user);
            return view.empty(chatId);
        }

        Page<UserWord> result = userWordStorage.search(user, context.query(), page);

        if (result.isEmpty()) {
            searchState.finish(user);
            return view.empty(chatId);
        }

        searchState.setPage(user, result.getNumber());

        return view.page(chatId, context.query(), result);
    }

    public void finish(TelegramUser user) {
        searchState.finish(user);
    }
}