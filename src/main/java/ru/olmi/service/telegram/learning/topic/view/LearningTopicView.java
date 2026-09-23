package ru.olmi.service.telegram.learning.topic.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.Topic;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.learning.topic.keyboard.LearningTopicKeyboard;

@Component
@RequiredArgsConstructor
public class LearningTopicView {
    private final TelegramMessage telegramMessage;
    private final LearningTopicKeyboard keyboard;

    public SendMessage topics(Long chatId, Page<Topic> result) {
        return telegramMessage.withKeyboard(chatId, "🎓 Выберите топик для обучения:",
                keyboard.topics(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage emptyTopics(Long chatId) {
        return telegramMessage.text(chatId, "У вас пока нет топиков.");
    }

    public SendMessage notEnoughWords(Long chatId) {
        return telegramMessage.text(chatId,
                "В этом топике недостаточно слов для обучения.\n" +
                        "Нужно минимум 4 слова."
        );
    }
}
