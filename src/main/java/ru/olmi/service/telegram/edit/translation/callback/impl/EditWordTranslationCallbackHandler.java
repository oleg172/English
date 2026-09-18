package ru.olmi.service.telegram.edit.translation.callback.impl;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.edit.state.EditWordDialogState;
import ru.olmi.service.telegram.edit.translation.EditWordTranslationView;
import ru.olmi.service.telegram.edit.translation.callback.EditWordTranslationCallback;
import ru.olmi.service.telegram.edit.translation.state.AddTranslationDialogState;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;

@Component
@RequiredArgsConstructor
public class EditWordTranslationCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserWordStorage userWordStorage;
    private final AddTranslationDialogState addTranslationDialog;
    private final EditWordDialogState editWordDialogState;
    private final EditWordTranslationView view;
    private final WordCardView wordCardView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return EditWordTranslationCallback.isAdd(data)
                || EditWordTranslationCallback.isDelete(data)
                || EditWordTranslationCallback.isRemove(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        if (EditWordTranslationCallback.isAdd(data)) {
            handleAddTranslation(callbackQuery, user, sender);
            return;
        }

        if (EditWordTranslationCallback.isDelete(data)) {
            handleDeleteTranslation(callbackQuery, user, sender);
            return;
        }

        if (EditWordTranslationCallback.isRemove(data)) {
            handleRemoveTranslation(callbackQuery, user, sender);
        }
    }

    //добавление нового перевода
    private void handleAddTranslation(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        Long userWordId = EditWordTranslationCallback.getAddUserWordId(callbackQuery.getData());
        EditWordDialogState.EditContext context = editWordDialogState.get(user);
        if (context == null) {
            sender.accept(view.sessionUnavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        addTranslationDialog.start(user, userWordId, context.source());

        sender.accept(view.enterTranslation(callbackQuery.getMessage().getChatId()));
    }

    //показывает все пользовательские переводы для удаления одного из них
    private void handleDeleteTranslation(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        Long userWordId = EditWordTranslationCallback.getDeleteUserWordId(callbackQuery.getData());

        List<UserWordTranslation> translations = userWordStorage.findTranslationsForEdit(user, userWordId);

        if (translations.isEmpty()) {
            sender.accept(view.noTranslations(callbackQuery.getMessage().getChatId(), userWordId));
            return;
        }

        sender.accept(view.selectTranslation(callbackQuery.getMessage().getChatId(), userWordId, translations));
    }

    //удаление пользовательского перевода
    private void handleRemoveTranslation(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        Long userWordId = EditWordTranslationCallback.getRemoveUserWordId(data);
        Long translationId = EditWordTranslationCallback.getRemoveTranslationId(data);

        boolean deleted = userWordStorage.deleteTranslation(user, translationId);
        if (!deleted) {
            sender.accept(view.unavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        Long chatId = callbackQuery.getMessage().getChatId();
        EditWordDialogState.EditContext context = editWordDialogState.get(user);

        userWordStorage.findForView(user, userWordId)
                       .ifPresentOrElse(
                               result -> sender.accept(wordCardView.showForEdit(chatId, result, userWordId, context.source())),
                               () -> sender.accept(wordCardView.unavailable(chatId))
                       );
    }
}
