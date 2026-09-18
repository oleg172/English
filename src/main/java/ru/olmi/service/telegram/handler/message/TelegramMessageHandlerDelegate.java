package ru.olmi.service.telegram.handler.message;

import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;

public interface TelegramMessageHandlerDelegate {

    boolean supports(Message message, TelegramUser user);

    void handle(Message message, TelegramUser user, Consumer<SendMessage> sender);
}
