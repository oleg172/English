package ru.olmi.service.telegram.dictionary.topic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class UserDictionaryTopicView {

    private final TelegramMessage telegramMessage;
    private final UserDictionaryTopicKeyboard keyboard;

    public SendMessage topics(Long chatId, Page<Topic> result) {
        return telegramMessage.withKeyboard(chatId, "📚 Топики", keyboard.topics(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage words(Long chatId, Topic topic, Page<UserWord> result) {
        return telegramMessage.withKeyboard(chatId, "📚 Топик: " + topic.getName(),
                keyboard.words(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage emptyTopics(Long chatId) {
        return telegramMessage.text(chatId, "У вас пока нет топиков.");
    }

    public SendMessage emptyWords(Long chatId, Topic topic) {
        return telegramMessage.text(chatId, "В топике «" + topic.getName() + "» нет слов.");
    }
}
