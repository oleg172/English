package ru.olmi.service.telegram.menu.main.callback;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.help.HelpView;
import ru.olmi.service.telegram.menu.dictionary.UserDictionaryMenuService;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.search.UserWordSearchView;
import ru.olmi.service.telegram.search.state.WordSearchDialogState;
import ru.olmi.service.telegram.translate.TranslationView;
import ru.olmi.service.telegram.translate.state.TranslationDialogState;
import ru.olmi.service.telegram.upload.DictionaryImportDialogState;
import ru.olmi.service.telegram.upload.DictionaryImportView;

@Component
@RequiredArgsConstructor
public class MainMenuCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final TranslationDialogState translationDialog;
    private final WordSearchDialogState wordSearchDialog;
    private final UserDictionaryMenuService userDictionaryService;
    private final DictionaryImportDialogState dictionaryImportDialog;
    private final TranslationView translationView;
    private final UserWordSearchView userWordSearchView;
    private final DictionaryImportView dictionaryImportView;
    private final HelpView helpView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return MainMenuCallback.TRANSLATE.equals(data)
                || MainMenuCallback.EDIT_WORD.equals(data)
                || MainMenuCallback.MY_WORDS.equals(data)
                || MainMenuCallback.IMPORT.equals(data)
                || MainMenuCallback.HELP.equals(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        if (MainMenuCallback.TRANSLATE.equals(data)) {
            startTranslation(callbackQuery, user, sender);
            return;
        }

        if (MainMenuCallback.EDIT_WORD.equals(data)) {
            startWordSearch(callbackQuery, user, sender);
        }

        if (MainMenuCallback.MY_WORDS.equals(data)) {
            showMyWords(callbackQuery, sender);
        }

        if (MainMenuCallback.IMPORT.equals(data)) {
            startImport(callbackQuery, user, sender);
            return;
        }

        if (MainMenuCallback.HELP.equals(data)) {
            showHelp(callbackQuery, sender);
        }
    }

    //поиск перевода(ввод текста и поиск перевода)
    private void startTranslation(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        translationDialog.start(user);

        sender.accept(translationView.enterWord(callbackQuery.getMessage().getChatId()));
    }

    //поиск слова в словаре(ввод текста и поиск похожих слови из словаря)
    private void startWordSearch(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        wordSearchDialog.start(user);

        sender.accept(userWordSearchView.enterQuery(callbackQuery.getMessage().getChatId()));
    }

    //показать 'Мой словарь' для пользователя
    private void showMyWords(CallbackQuery callbackQuery, Consumer<SendMessage> sender) {
        sender.accept(userDictionaryService.show(callbackQuery.getMessage().getChatId()));
    }

    private void startImport(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        dictionaryImportDialog.start(user);

        sender.accept(dictionaryImportView.enterFile(callbackQuery.getMessage().getChatId()));
    }

    private void showHelp(CallbackQuery callbackQuery, Consumer<SendMessage> sender) {
        sender.accept(helpView.show(callbackQuery.getMessage().getChatId()));
    }
}
