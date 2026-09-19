package ru.olmi.service.telegram.upload.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.menu.main.MainMenuView;
import ru.olmi.service.telegram.upload.callback.DictionaryImportCallback;

@Component
@RequiredArgsConstructor
public class DictionaryImportCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final MainMenuView mainMenuView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        return DictionaryImportCallback.MAIN_MENU.equals(callbackQuery.getData());
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        sender.accept(mainMenuView.show(callbackQuery.getMessage().getChatId()));
    }
}
