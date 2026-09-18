package ru.olmi.service.telegram.dictionary.topic.search.word;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicWordSearchView {
    private final TelegramMessage telegramMessage;
    private final UserDictionaryTopicWordSearchKeyboard keyboard;

    public SendMessage enterQuery(Long chatId) {
        return telegramMessage.text(chatId, "🔎 Введите слово:");
    }

    public SendMessage results(Long chatId, Topic topic, String query, Page<UserWord> result) {
        return telegramMessage.withKeyboard(chatId, "🔎 Поиск в топике «" + topic.getName() + "»: " + query,
                keyboard.results(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage empty(Long chatId, String query) {
        return telegramMessage.text(chatId, "По запросу «" + query + "» слова не найдены.");
    }

    public SendMessage topicUnavailable(Long chatId) {
        return telegramMessage.text(chatId, "Топик больше недоступен.");
    }
}