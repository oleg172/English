package ru.olmi.service.telegram.dictionary.topic.search.topic.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchService;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchState;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicSearchMessageHandler implements TelegramMessageHandlerDelegate {
    private final UserDictionaryTopicSearchState state;
    private final UserDictionaryTopicSearchService searchService;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return state.isWaitingForQuery(user) && message.hasText();
    }

    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        sender.accept(searchService.search(user, message.getChatId(), message.getText()));
    }
}
