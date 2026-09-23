package ru.olmi.service.telegram.learning.menu.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.learning.menu.LearningMenuView;
import ru.olmi.service.telegram.learning.menu.callback.LearningMenuCallback;
import ru.olmi.service.telegram.learning.topic.LearningTopicService;
import ru.olmi.service.telegram.menu.main.MainMenuView;

@Component
@RequiredArgsConstructor
public class LearningMenuCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final MainMenuView mainMenuView;
    private final LearningTopicService learningTopicService;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return LearningMenuCallback.MENU.equals(data)
                || LearningMenuCallback.TOPIC.equals(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (LearningMenuCallback.MENU.equals(data)) {
            sender.accept(mainMenuView.show(chatId));
            return;
        }

        if (LearningMenuCallback.TOPIC.equals(data)) {
            sender.accept(learningTopicService.topics(user, chatId));
        }
    }
}
