package ru.olmi.service.telegram.dictionary.topic.search.topic.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchService;
import ru.olmi.service.telegram.dictionary.topic.search.topic.callback.UserDictionaryTopicSearchCallback;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicSearchCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserDictionaryTopicSearchService searchService;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return UserDictionaryTopicSearchCallback.isSearch(data)
                || UserDictionaryTopicSearchCallback.isNext(data)
                || UserDictionaryTopicSearchCallback.isPrevious(data)
                || UserDictionaryTopicSearchCallback.isCurrent(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (UserDictionaryTopicSearchCallback.isSearch(data)) {
            sender.accept(searchService.start(user, chatId));
            return;
        }

        if (UserDictionaryTopicSearchCallback.isNext(data)) {
            sender.accept(searchService.next(user, chatId));
            return;
        }

        if (UserDictionaryTopicSearchCallback.isPrevious(data)) {
            sender.accept(searchService.previous(user, chatId));
            return;
        }

        if (UserDictionaryTopicSearchCallback.isCurrent(data)) {
            sender.accept(searchService.current(user, chatId));
        }
    }
}
