package ru.olmi.service.telegram.learning.topic.selection.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerWithEditDelegate;
import ru.olmi.service.telegram.learning.topic.LearningTopicService;
import ru.olmi.service.telegram.learning.topic.selection.LearningWordSelectionService;
import ru.olmi.service.telegram.learning.topic.selection.callback.LearningWordSelectionCallback;

@Component
@RequiredArgsConstructor
public class LearningWordSelectionCallbackHandler implements TelegramCallbackHandlerWithEditDelegate {

    private final LearningWordSelectionService selectionService;
    private final LearningTopicService learningTopicService;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return LearningWordSelectionCallback.isWord(data)
                || LearningWordSelectionCallback.isNext(data)
                || LearningWordSelectionCallback.isPrevious(data)
                || LearningWordSelectionCallback.isCurrent(data)
                || LearningWordSelectionCallback.isBack(data)
                || LearningWordSelectionCallback.isConfirm(data);
    }

    @Override
    public void handle(
            CallbackQuery callbackQuery,
            TelegramUser user,
            Consumer<SendMessage> sender,
            Consumer<EditMessageText> editor
    ) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (LearningWordSelectionCallback.isWord(data)) {
            Long userWordId = LearningWordSelectionCallback.getUserWordId(data);

            editor.accept(selectionService.toggleWord(user, chatId, messageId, userWordId));
            return;
        }

        if (LearningWordSelectionCallback.isNext(data)) {
            editor.accept(selectionService.next(user, chatId, messageId));
            return;
        }

        if (LearningWordSelectionCallback.isPrevious(data)) {
            editor.accept(selectionService.previous(user, chatId, messageId));
            return;
        }

        if (LearningWordSelectionCallback.isCurrent(data)) {
            editor.accept(selectionService.current(user, chatId, messageId));
            return;
        }

        if (LearningWordSelectionCallback.isConfirm(data)) {
            sender.accept(selectionService.confirm(user, chatId));
            return;
        }

        if (LearningWordSelectionCallback.isBack(data)) {
            editor.accept(learningTopicService.editTopics(user, chatId, messageId));
        }
    }
}
