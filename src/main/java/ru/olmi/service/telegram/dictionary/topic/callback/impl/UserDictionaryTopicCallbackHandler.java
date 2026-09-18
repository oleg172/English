package ru.olmi.service.telegram.dictionary.topic.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.dictionary.topic.UserDictionaryTopicService;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.menu.main.MainMenuView;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserDictionaryTopicService topicService;
    private final MainMenuView mainMenuView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return UserDictionaryTopicCallback.isTopics(data)
                || UserDictionaryTopicCallback.isNext(data)
                || UserDictionaryTopicCallback.isPrevious(data)
                || UserDictionaryTopicCallback.isCurrent(data)
                || UserDictionaryTopicCallback.isTopic(data)
                || UserDictionaryTopicCallback.isMenu(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (UserDictionaryTopicCallback.isTopics(data)) {
            sender.accept(topicService.currentTopics(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isNext(data)) {
            sender.accept(topicService.nextTopics(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isPrevious(data)) {
            sender.accept(topicService.previousTopics(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isCurrent(data)) {
            sender.accept(topicService.currentTopics(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isTopic(data)) {
            sender.accept(topicService.selectTopic(user, chatId, UserDictionaryTopicCallback.getTopicId(data)));
            return;
        }

        if (UserDictionaryTopicCallback.isMenu(data)) {
            sender.accept(mainMenuView.show(chatId));
        }
    }
}
