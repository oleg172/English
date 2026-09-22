package ru.olmi.service.telegram.export.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.export.DictionaryExportService;
import ru.olmi.service.telegram.export.callback.DictionaryExportCallback;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.menu.main.MainMenuView;

@Component
@RequiredArgsConstructor
public class DictionaryExportCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final DictionaryExportService exportService;
    private final MainMenuView mainMenuView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        return DictionaryExportCallback.EXPORT.equals(callbackQuery.getData());
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        exportService.export(user, callbackQuery.getMessage().getChatId());
        sender.accept(mainMenuView.show(callbackQuery.getMessage().getChatId()));
    }
}
