package ru.olmi.service.telegram.edit.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.TranslationResult;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.edit.EditWordView;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.edit.state.EditWordDialogState;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.menu.main.MainMenuView;
import ru.olmi.service.telegram.search.UserWordSearch;

@Component
@RequiredArgsConstructor
public class EditWordCallbackHandler implements TelegramCallbackHandlerDelegate {
    private final UserWordStorage userWordStorage;
    private final EditWordDialogState editWordDialogState;
    private final UserWordSearch userWordSearch;

    private final WordCardView wordCardView;
    private final EditWordView editWordView;
    private final MainMenuView mainMenuView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return EditWordCallback.MENU.equals(data)
                || EditWordCallback.isWord(data)
                || EditWordCallback.isEdit(data)
                || EditWordCallback.isEditMenu(data)
                || EditWordCallback.isDelete(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        if (EditWordCallback.MENU.equals(data)) {
            handleMainMenu(callbackQuery, user, sender);
            return;
        }

        if (EditWordCallback.isWord(data)) {
            handleWord(callbackQuery, user, sender);
            return;
        }

        if (EditWordCallback.isEdit(data)) {
            handleEdit(callbackQuery, user, sender);
            return;
        }

        if (EditWordCallback.isEditMenu(data)) {
            handleEditMenuBack(callbackQuery, user, sender);
            return;
        }

        if (EditWordCallback.isDelete(data)) {
            handleDelete(callbackQuery, user, sender);
        }
    }

    private void handleMainMenu(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        userWordSearch.finish(user);

        sender.accept(mainMenuView.show(callbackQuery.getMessage().getChatId()));
    }

    private void handleWord(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        Long userWordId = EditWordCallback.getUserWordId(callbackQuery.getData());
        Long chatId = callbackQuery.getMessage().getChatId();

        TranslationResult result = userWordStorage.findForView(user, userWordId)
                                                  .orElse(null);

        if (result == null) {
            sender.accept(wordCardView.unavailable(chatId));
            return;
        }

        sender.accept(wordCardView.showForEdit(chatId, result, userWordId, EditWordSource.SEARCH));
    }

    private void handleEdit(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        Long userWordId = EditWordCallback.getEditUserWordId(callbackQuery.getData());
        EditWordSource source = EditWordCallback.getEditSource(callbackQuery.getData());

        TranslationResult result = userWordStorage.findForView(user, userWordId)
                                                  .orElse(null);

        if (result == null) {
            sender.accept(wordCardView.unavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        editWordDialogState.start(user, userWordId, source);
        sender.accept(editWordView.editMenu(callbackQuery.getMessage().getChatId(), result.getWord(), userWordId));
    }

    private void handleEditMenuBack(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        EditWordDialogState.EditContext context = editWordDialogState.get(user);
        Long chatId = callbackQuery.getMessage().getChatId();

        if (context == null) {
            sender.accept(userWordSearch.current(user, chatId));
            return;
        }

        TranslationResult result = userWordStorage.findForView(user, context.userWordId())
                                                  .orElse(null);

        if (result == null) {
            editWordDialogState.finish(user);
            sender.accept(wordCardView.unavailable(chatId));
            return;
        }

        sender.accept(wordCardView.showForEdit(chatId, result, context.userWordId(), context.source()));
    }

    private void handleDelete(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long userWordId = EditWordCallback.getDeleteUserWordId(data);
        Long chatId = callbackQuery.getMessage().getChatId();

        boolean deleted = userWordStorage.delete(user, userWordId);

        if (!deleted) {
            sender.accept(wordCardView.unavailable(chatId));
            return;
        }

        sender.accept(userWordSearch.current(user, chatId));
    }
}
