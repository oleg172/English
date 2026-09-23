package ru.olmi.service.telegram.learning.topic.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.learning.menu.LearningMenuView;
import ru.olmi.service.telegram.learning.topic.LearningTopicService;
import ru.olmi.service.telegram.learning.topic.callback.LearningTopicCallback;

@Component
@RequiredArgsConstructor
public class LearningTopicCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final LearningTopicService learningTopicService;
    private final LearningMenuView learningMenuView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return LearningTopicCallback.isTopics(data)
                || LearningTopicCallback.isNext(data)
                || LearningTopicCallback.isPrevious(data)
                || LearningTopicCallback.isCurrent(data)
                || LearningTopicCallback.isTopic(data)
                || LearningTopicCallback.isMenu(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (LearningTopicCallback.isTopics(data)) {
            sender.accept(learningTopicService.topics(user, chatId));
            return;
        }

        if (LearningTopicCallback.isNext(data)) {
            sender.accept(learningTopicService.nextTopics(user, chatId));
            return;
        }

        if (LearningTopicCallback.isPrevious(data)) {
            sender.accept(learningTopicService.previousTopics(user, chatId));
            return;
        }

        if (LearningTopicCallback.isCurrent(data)) {
            sender.accept(learningTopicService.currentTopics(user, chatId));
            return;
        }

        if (LearningTopicCallback.isTopic(data)) {
            Long topicId = LearningTopicCallback.getTopicId(data);
            sender.accept(learningTopicService.selectTopic(user, chatId, topicId));
            return;
        }

        if (LearningTopicCallback.isMenu(data)) {
            sender.accept(learningMenuView.show(chatId));
        }
    }
}
