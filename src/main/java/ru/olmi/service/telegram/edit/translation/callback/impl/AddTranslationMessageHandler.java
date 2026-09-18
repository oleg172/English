package ru.olmi.service.telegram.edit.translation.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.edit.translation.EditWordTranslationView;
import ru.olmi.service.telegram.edit.translation.state.AddTranslationDialogState;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;

@Component
@RequiredArgsConstructor
public class AddTranslationMessageHandler implements TelegramMessageHandlerDelegate {

    private final AddTranslationDialogState dialogState;
    private final UserWordStorage userWordStorage;
    private final EditWordTranslationView view;
    private final WordCardView wordCardView;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return dialogState.isWaiting(user);
    }

    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        String translation = message.getText().trim();

        if (translation.isEmpty()) {
            dialogState.finish(user);
            return;
        }

        AddTranslationDialogState.AddTranslationContext context = dialogState.get(user);
        if (context == null) {
            dialogState.finish(user);
            return;
        }

        Long userWordId = context.userWordId();
        if (userWordId == null) {
            dialogState.finish(user);
            return;
        }

        userWordStorage.addTranslation(user, userWordId, translation);
        dialogState.finish(user);
        userWordStorage.findForView(user, userWordId)
                       .ifPresentOrElse(
                               result -> sender.accept(wordCardView.showForEdit(message.getChatId(), result, userWordId, context.source())),
                               () -> sender.accept(wordCardView.unavailable(message.getChatId()))
                       );
    }
}
