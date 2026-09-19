package ru.olmi.service.telegram.dictionary.topic.search.topic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.Topic;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicSearchView {

    private final TelegramMessage telegramMessage;
    private final UserDictionaryTopicSearchKeyboard keyboard;

    public SendMessage enterQuery(Long chatId) {
        return telegramMessage.text(chatId, "🔎 Введите название топика:");
    }

    public SendMessage results(Long chatId, String query, Page<Topic> result) {
        return telegramMessage.withKeyboard(chatId, "🔎 Результаты поиска: " + query,
                keyboard.results(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage empty(Long chatId, String query) {
        return telegramMessage.withKeyboard(chatId, "По запросу «" + query + "» топики не найдены.", keyboard.empty());
    }
}
