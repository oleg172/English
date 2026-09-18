package ru.olmi.service.telegram.handler.callback;

import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;

public interface TelegramCallbackHandlerDelegate {

    boolean supports(CallbackQuery callbackQuery);

    void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender);
}
