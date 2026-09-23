package ru.olmi.service.telegram.learning.topic.selection.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.dto.TrainingAnswerResult;
import ru.olmi.dto.TrainingMistake;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.learning.topic.selection.keyboard.TrainingResultKeyboard;

@Component
@RequiredArgsConstructor
public class TrainingResultTelegramView {

    private final TelegramMessage telegramMessage;
    private final TrainingResultKeyboard keyboard;

    public SendMessage completed(Long chatId, TrainingAnswerResult result) {
        StringBuilder text = new StringBuilder();

        text.append("🎓 Обучение завершено!\n\n")
            .append("Всего слов: ")
            .append(result.totalWords())
            .append("\n")
            .append("✅ Правильных: ")
            .append(result.correctAnswers())
            .append("\n")
            .append("❌ Ошибок: ")
            .append(result.incorrectAnswers());

        if (!result.mistakes().isEmpty()) {
            text.append("\n\n")
                .append("❌ Ошибки:\n");

            for (TrainingMistake mistake : result.mistakes()) {
                text.append("\n")
                    .append("• ")
                    .append(mistake.word())
                    .append("\n")
                    .append("  Ваш ответ: ")
                    .append(mistake.selectedAnswer())
                    .append("\n")
                    .append("  Правильно: ")
                    .append(mistake.correctAnswer())
                    .append("\n");
            }
        }

        return telegramMessage.withKeyboard(chatId, text.toString(), keyboard.completed());
    }
}
