package ru.olmi.service.telegram.dictionary.allwords;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWord;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.dictionary.allwords.state.UserDictionaryDialogState;

@Component
@RequiredArgsConstructor
public class UserDictionaryListService {

    private final UserWordStorage userWordStorage;
    private final UserDictionaryView view;
    private final UserDictionaryDialogState state;

    public SendMessage list(TelegramUser user, Long chatId) {
        state.start(user);

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
        Page<UserWord> result = userWordStorage.findPage(user, page);

        if (result.isEmpty() && page > 0) {
            return page(user, chatId, page - 1);
        }

        if (result.isEmpty()) {
            return view.empty(chatId);
        }

        state.setPage(user, result.getNumber());

        return view.page(chatId, result);
    }
}
