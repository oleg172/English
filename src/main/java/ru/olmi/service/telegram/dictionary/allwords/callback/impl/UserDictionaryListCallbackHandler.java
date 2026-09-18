package ru.olmi.service.telegram.dictionary.allwords.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.dictionary.allwords.UserDictionaryListService;
import ru.olmi.service.telegram.dictionary.allwords.callback.UserDictionaryCallback;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;

@Component
@RequiredArgsConstructor
public class UserDictionaryListCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserDictionaryListService userDictionaryService;
    private final UserWordStorage userWordStorage;
    private final WordCardView wordCardView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return UserDictionaryCallback.LIST.equals(data)
                || UserDictionaryCallback.isNext(data)
                || UserDictionaryCallback.isPrevious(data)
                || UserDictionaryCallback.isCurrent(data)
                || UserDictionaryCallback.isWord(data)
                || UserDictionaryCallback.isBack(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        Long chatId = callbackQuery.getMessage().getChatId();

        if (UserDictionaryCallback.LIST.equals(data)) {
            sender.accept(userDictionaryService.list(user, chatId));
            return;
        }

        if (UserDictionaryCallback.isNext(data)) {
            sender.accept(userDictionaryService.next(user, chatId));
            return;
        }

        if (UserDictionaryCallback.isPrevious(data)) {
            sender.accept(userDictionaryService.previous(user, chatId));
            return;
        }

        if (UserDictionaryCallback.isCurrent(data)) {
            sender.accept(userDictionaryService.current(user, chatId));
            return;
        }

        if (UserDictionaryCallback.isWord(data)) {
            Long userWordId = UserDictionaryCallback.getUserWordId(data);

            showWord(callbackQuery, user, userWordId, sender);
            return;
        }

        if (UserDictionaryCallback.isBack(data)) {
            sender.accept(userDictionaryService.current(user, chatId));
        }
    }

    private void showWord(CallbackQuery callbackQuery, TelegramUser user, Long userWordId, Consumer<SendMessage> sender) {
        Long chatId = callbackQuery.getMessage().getChatId();

        userWordStorage.findForView(user, userWordId)
                       .ifPresentOrElse(
                               result -> sender.accept(wordCardView.showForEdit(chatId, result, userWordId, EditWordSource.DICTIONARY)),
                               () -> sender.accept(wordCardView.unavailable(chatId))
                       );
    }
}
