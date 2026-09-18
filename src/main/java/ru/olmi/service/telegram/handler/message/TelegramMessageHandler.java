package ru.olmi.service.telegram.handler.message;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.user.TelegramUserResolver;

@Component
@RequiredArgsConstructor
public class TelegramMessageHandler {

    private final TelegramUserResolver userResolver;
    private final List<TelegramMessageHandlerDelegate> handlers;

    public void handle(Message message, Consumer<SendMessage> sender) {
        TelegramUser user = userResolver.resolve(message.getFrom());
        handlers.stream()
                .filter(handler -> handler.supports(message, user))
                .findFirst()
                .ifPresent(handler -> handler.handle(message, user, sender));
    }
}