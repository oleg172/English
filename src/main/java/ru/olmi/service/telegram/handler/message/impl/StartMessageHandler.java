package ru.olmi.service.telegram.handler.message.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;
import ru.olmi.service.telegram.menu.main.MainMenuView;

@Component
@RequiredArgsConstructor
public class StartMessageHandler implements TelegramMessageHandlerDelegate {

    private final MainMenuView mainMenuView;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return "/start".equals(message.getText());
    }

    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        sender.accept(mainMenuView.show(message.getChatId()));
    }
}
