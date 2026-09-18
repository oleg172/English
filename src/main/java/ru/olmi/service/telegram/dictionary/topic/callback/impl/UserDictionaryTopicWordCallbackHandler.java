package ru.olmi.service.telegram.dictionary.topic.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.common.WordCardView;
import ru.olmi.service.telegram.dictionary.topic.UserDictionaryTopicService;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicWordCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserDictionaryTopicService topicService;
    private final UserWordStorage userWordStorage;
    private final WordCardView wordCardView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return UserDictionaryTopicCallback.isWordNext(data)
                || UserDictionaryTopicCallback.isWordPrevious(data)
                || UserDictionaryTopicCallback.isWordCurrent(data)
                || UserDictionaryTopicCallback.isWord(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (UserDictionaryTopicCallback.isWordNext(data)) {
            sender.accept(topicService.nextWords(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isWordPrevious(data)) {
            sender.accept(topicService.previousWords(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isWordCurrent(data)) {
            sender.accept(topicService.currentWords(user, chatId));
            return;
        }

        if (UserDictionaryTopicCallback.isWord(data)) {
            showWord(callbackQuery, user, UserDictionaryTopicCallback.getUserWordId(data), sender);
        }
    }

    private void showWord(CallbackQuery callbackQuery, TelegramUser user, Long userWordId, Consumer<SendMessage> sender) {
        Long chatId = callbackQuery.getMessage().getChatId();

        userWordStorage.findForView(user, userWordId)
                       .ifPresentOrElse(result -> sender.accept(wordCardView.showForEdit(chatId, result, userWordId, EditWordSource.TOPIC)),
                               () -> sender.accept(wordCardView.unavailable(chatId)));
    }
}
