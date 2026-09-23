package ru.olmi.service.telegram.learning.topic.selection.view;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.learning.topic.selection.keyboard.LearningWordSelectionKeyboard;

@Component
@RequiredArgsConstructor
public class LearningWordSelectionView {

    private final TelegramMessage telegramMessage;
    private final LearningWordSelectionKeyboard keyboard;

    public SendMessage words(Long chatId, Topic topic, Page<UserWord> result, Set<Long> selectedWordIds) {
        return telegramMessage.withKeyboard(
                chatId,
                "🎓 Выберите минимум 4 слова для обучения\n" +
                        "Топик: " + topic.getName(),
                keyboard.words(
                        result.getContent(),
                        selectedWordIds,
                        result.getNumber(),
                        result.getTotalPages()
                )
        );
    }

    public SendMessage notEnoughSelectedWords(Long chatId) {
        return telegramMessage.text(chatId, "Нужно выбрать минимум 4 слова.");
    }
}
