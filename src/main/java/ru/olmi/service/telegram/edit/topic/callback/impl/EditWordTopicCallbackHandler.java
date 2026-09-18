package ru.olmi.service.telegram.edit.topic.callback.impl;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWordTopic;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.edit.state.EditWordDialogState;
import ru.olmi.service.telegram.edit.topic.EditWordTopicView;
import ru.olmi.service.telegram.edit.topic.callback.EditWordTopicCallback;
import ru.olmi.service.telegram.edit.topic.state.AddTopicDialogState;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;

@Component
@RequiredArgsConstructor
public class EditWordTopicCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final AddTopicDialogState addTopicDialog;
    private final TopicStorage topicStorage;
    private final EditWordDialogState editWordDialogState;
    private final EditWordTopicView view;
    private final UserWordStorage userWordStorage;
    private final WordCardView wordCardView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return EditWordTopicCallback.isAdd(data)
                || EditWordTopicCallback.isDelete(data)
                || EditWordTopicCallback.isRemove(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        if (EditWordTopicCallback.isAdd(data)) {
            handleAddTopic(callbackQuery, user, sender);
            return;
        }

        if (EditWordTopicCallback.isDelete(data)) {
            handleDeleteTopic(callbackQuery, user, sender);
            return;
        }

        if (EditWordTopicCallback.isRemove(data)) {
            handleRemoveTopic(callbackQuery, user, sender);
        }
    }

    /**
     * добавление топика
     */
    private void handleAddTopic(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        Long userWordId = EditWordTopicCallback.getAddUserWordId(callbackQuery.getData());

        EditWordDialogState.EditContext context = editWordDialogState.get(user);

        if (context == null) {
            sender.accept(view.sessionUnavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        addTopicDialog.start(user, userWordId, context.source());

        sender.accept(view.enterTopicName(callbackQuery.getMessage().getChatId()));
    }

    /**
     * показ всех топиков слова для их удаления
     * */
    private void handleDeleteTopic(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        Long userWordId = EditWordTopicCallback.getDeleteUserWordId(callbackQuery.getData());

        List<UserWordTopic> topics = topicStorage.findTopicsForEdit(user, userWordId);

        if (topics.isEmpty()) {
            sender.accept(view.noTopics(callbackQuery.getMessage().getChatId(), userWordId));
            return;
        }

        sender.accept(view.selectTopic(callbackQuery.getMessage().getChatId(), userWordId, topics));
    }

    /**
     * удаления топика слова
     */
    private void handleRemoveTopic(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long userWordId = EditWordTopicCallback.getRemoveUserWordId(data);
        Long userWordTopicId = EditWordTopicCallback.getRemoveUserWordTopicId(data);

        boolean deleted = topicStorage.deleteTopic(user, userWordId, userWordTopicId);
        Long chatId = callbackQuery.getMessage().getChatId();
        if (!deleted) {
            sender.accept(view.unavailable(chatId));
            return;
        }

        EditWordDialogState.EditContext context = editWordDialogState.get(user);

        if (context == null) {
            sender.accept(wordCardView.unavailable(chatId));
            return;
        }

        userWordStorage.findForView(user, userWordId)
                       .ifPresentOrElse(
                               result -> sender.accept(wordCardView.showForEdit(chatId, result, userWordId, context.source())),
                               () -> sender.accept(wordCardView.unavailable(chatId))
                       );
    }
}
