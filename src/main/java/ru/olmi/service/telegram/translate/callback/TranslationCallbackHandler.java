package ru.olmi.service.telegram.translate.callback;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Word;
import ru.olmi.dto.TranslationResult;
import ru.olmi.repository.WordRepository;
import ru.olmi.service.impl.TranslationFinder;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.menu.main.MainMenuView;
import ru.olmi.service.telegram.translate.TranslationView;
import ru.olmi.service.telegram.translate.state.TranslationDialogState;

@Component
@RequiredArgsConstructor
public class TranslationCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final WordRepository wordRepository;
    private final UserWordStorage userWordStorage;
    private final TranslationDialogState state;
    private final MainMenuView mainMenuView;
    private final TranslationView translationView;
    private final TranslationFinder translationFinder;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return TranslationCallback.isAddToDictionary(data)
                || TranslationCallback.isSelect(data)
                || TranslationCallback.MAIN_MENU.equals(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        if (TranslationCallback.isAddToDictionary(data)) {
            handleAddToDictionary(callbackQuery, user, TranslationCallback.getWordId(data), sender);
            return;
        }
        if (TranslationCallback.isSelect(data)) {
            handleSelect(callbackQuery, user, TranslationCallback.getSelectedWordId(data), sender);
            return;
        }

        handleMainMenu(callbackQuery, user, sender);
    }

    /**
     * Обрабатывает добавления слова в пользовательский словарь
     */
    private void handleAddToDictionary(CallbackQuery callbackQuery, TelegramUser user, Long wordId, Consumer<SendMessage> sender) {
        Word word = wordRepository.findById(wordId).orElse(null);

        if (word == null) {
            state.finish(user);
            sender.accept(translationView.unavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        userWordStorage.findOrCreate(user, word);

        state.finish(user);

        sender.accept(mainMenuView.show(callbackQuery.getMessage().getChatId(), "✅ Слово добавлено в словарь.\n\n"));
    }

    private void handleSelect(CallbackQuery callbackQuery, TelegramUser user, Long wordId, Consumer<SendMessage> sender) {
        Word word = wordRepository.findById(wordId).orElse(null);

        if (word == null) {
            state.finish(user);
            sender.accept(translationView.unavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        TranslationResult result = translationFinder.searchByWord(user, word.getWord());

        if (result == null) {
            state.finish(user);
            sender.accept(translationView.unavailable(callbackQuery.getMessage().getChatId()));
            return;
        }

        state.finish(user);
        sender.accept(translationView.result(callbackQuery.getMessage().getChatId(), result));
    }

    /**
     * Возврашение в главное меню
     */
    private void handleMainMenu(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        state.finish(user);
        sender.accept(mainMenuView.show(callbackQuery.getMessage().getChatId()));
    }
}
