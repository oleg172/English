package ru.olmi.service.telegram.translate.callback;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.TranslationResult;
import ru.olmi.dto.TranslationSearchResult;
import ru.olmi.service.impl.TranslationFinder;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;
import ru.olmi.service.telegram.translate.TranslationView;
import ru.olmi.service.telegram.translate.state.TranslationDialogState;

/**
 * Обработчик входящего сообщения для перевода
 */
@Component
@RequiredArgsConstructor
public class TranslationMessageHandler implements TelegramMessageHandlerDelegate {

    private final TranslationDialogState state;
    private final TranslationFinder translationFinder;
    private final TranslationView view;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return state.isWaitingForWord(user);
    }

    /**
     * Поиск перевода слова и сохранения результата перевода в общий словарь слов
     */
    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        String word = message.getText().trim();

        if (word.isEmpty()) {
            return;
        }

        TranslationSearchResult searchResult = translationFinder.search(user, word);

        if (searchResult.getResults().isEmpty()) {
            state.finish(user);
            sender.accept(view.notFound(message.getChatId()));
            return;
        }

        if (searchResult.getResults().size() == 1) {
            sender.accept(view.result(message.getChatId(), searchResult.getResults().get(0)));
            return;
        }

        sender.accept(view.results(message.getChatId(), searchResult.getResults()));
    }
}
