package ru.olmi.service.telegram.edit.search.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.edit.search.EditWordSearchCallback;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.search.UserWordSearch;

@Component
@RequiredArgsConstructor
public class EditWordSearchCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserWordSearch userWordSearch;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return EditWordSearchCallback.RESULTS.equals(data)
                || EditWordSearchCallback.NEXT.equals(data)
                || EditWordSearchCallback.PREVIOUS.equals(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (EditWordSearchCallback.RESULTS.equals(data)) {
            sender.accept(userWordSearch.current(user, chatId));
            return;
        }

        if (EditWordSearchCallback.NEXT.equals(data)) {
            sender.accept(userWordSearch.next(user, chatId));
            return;
        }

        if (EditWordSearchCallback.PREVIOUS.equals(data)) {
            sender.accept(userWordSearch.previous(user, chatId));
        }
    }
}