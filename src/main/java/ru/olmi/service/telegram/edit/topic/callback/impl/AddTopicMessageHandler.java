package ru.olmi.service.telegram.edit.topic.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.edit.topic.EditWordTopicView;
import ru.olmi.service.telegram.edit.topic.state.AddTopicDialogState;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;

@Component
@RequiredArgsConstructor
public class AddTopicMessageHandler implements TelegramMessageHandlerDelegate {

    private final AddTopicDialogState dialogState;
    private final TopicStorage topicStorage;
    private final UserWordStorage userWordStorage;
    private final WordCardView wordCardView;
    private final EditWordTopicView view;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return dialogState.isWaiting(user);
    }

    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        String topicName = message.getText();

        if (topicName == null || topicName.isBlank()) {
            dialogState.finish(user);
            sender.accept(view.emptyTopic(message.getChatId()));
            return;
        }

        topicName = topicName.trim();
        AddTopicDialogState.AddTopicContext context = dialogState.get(user);

        if (context == null) {
            dialogState.finish(user);
            return;
        }

        Long userWordId = context.userWordId();
        topicStorage.addTopic(user, userWordId, topicName);
        dialogState.finish(user);

        userWordStorage.findForView(user, userWordId)
                       .ifPresentOrElse(result -> sender.accept(wordCardView.showForEdit(message.getChatId(), result, userWordId, context.source())),
                               () -> sender.accept(wordCardView.unavailable(message.getChatId())));
    }
}
