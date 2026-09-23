package ru.olmi.service.telegram.learning.topic.selection.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.learning.topic.LearningTopicService;
import ru.olmi.service.telegram.learning.topic.selection.LearningWordSelectionService;
import ru.olmi.service.telegram.learning.topic.selection.callback.LearningWordSelectionCallback;

@Component
@RequiredArgsConstructor
public class LearningWordSelectionCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final LearningWordSelectionService selectionService;
    private final LearningTopicService learningTopicService;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return LearningWordSelectionCallback.isWord(data)
                || LearningWordSelectionCallback.isNext(data)
                || LearningWordSelectionCallback.isPrevious(data)
                || LearningWordSelectionCallback.isCurrent(data)
                || LearningWordSelectionCallback.isConfirm(data)
                || LearningWordSelectionCallback.isBack(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (LearningWordSelectionCallback.isWord(data)) {
            Long userWordId = LearningWordSelectionCallback.getUserWordId(data);

            sender.accept(selectionService.toggleWord(user, chatId, userWordId));
            return;
        }

        if (LearningWordSelectionCallback.isNext(data)) {
            sender.accept(selectionService.next(user, chatId));
            return;
        }

        if (LearningWordSelectionCallback.isPrevious(data)) {
            sender.accept(selectionService.previous(user, chatId));
            return;
        }

        if (LearningWordSelectionCallback.isCurrent(data)) {
            sender.accept(selectionService.current(user, chatId));
            return;
        }

        if (LearningWordSelectionCallback.isConfirm(data)) {
            sender.accept(selectionService.confirm(user, chatId));
            return;
        }

        if (LearningWordSelectionCallback.isBack(data)) {
            sender.accept(learningTopicService.topics(user, chatId));
        }
    }
}
