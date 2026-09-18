package ru.olmi.service.telegram.dictionary.topic.search.word;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicWordSearchCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserDictionaryTopicWordSearchService searchService;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return UserDictionaryTopicWordSearchCallback.isSearch(data)
                || UserDictionaryTopicWordSearchCallback.isNext(data)
                || UserDictionaryTopicWordSearchCallback.isPrevious(data)
                || UserDictionaryTopicWordSearchCallback.isCurrent(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (UserDictionaryTopicWordSearchCallback.isSearch(data)) {
            sender.accept(searchService.start(user, chatId));
            return;
        }

        if (UserDictionaryTopicWordSearchCallback.isNext(data)) {
            sender.accept(searchService.next(user, chatId));
            return;
        }

        if (UserDictionaryTopicWordSearchCallback.isPrevious(data)) {
            sender.accept(searchService.previous(user, chatId));
            return;
        }

        if (UserDictionaryTopicWordSearchCallback.isCurrent(data)) {
            sender.accept(searchService.current(user, chatId));
        }
    }
}
