package ru.olmi.service.telegram.edit.topic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.UserWordTopic;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.edit.keyboard.EditWordMenuKeyboard;
import ru.olmi.service.telegram.edit.topic.keyboard.DeleteTopicKeyboard;

@Component
@RequiredArgsConstructor
public class EditWordTopicView {

    private final TelegramMessage telegramMessage;
    private final EditWordMenuKeyboard editWordMenuKeyboard;
    private final DeleteTopicKeyboard deleteTopicKeyboard;

    public SendMessage emptyTopic(Long chatId) {
        return telegramMessage.text(chatId, "Топик не может быть пустым.");
    }

    public SendMessage sessionUnavailable(Long chatId) {
        return telegramMessage.text(chatId, "Сессия редактирования больше недоступна.");
    }

    public SendMessage enterTopicName(Long chatId) {
        return telegramMessage.text(chatId, "Введите название топика:");
    }

    public SendMessage noTopics(Long chatId, Long userWordId) {
        return telegramMessage.withKeyboard(chatId, "У слова нет топиков.", editWordMenuKeyboard.create(userWordId));
    }

    public SendMessage selectTopic(Long chatId, Long userWordId, List<UserWordTopic> topics) {
        return telegramMessage.withKeyboard(chatId, "Выберите топик для удаления:", deleteTopicKeyboard.create(userWordId, topics));
    }

    public SendMessage unavailable(Long chatId) {
        return telegramMessage.text(chatId, "Топик больше недоступен.");
    }
}