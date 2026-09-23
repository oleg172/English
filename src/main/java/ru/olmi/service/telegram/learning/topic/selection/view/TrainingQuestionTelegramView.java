package ru.olmi.service.telegram.learning.topic.selection.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.dto.TrainingQuestionData;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.learning.topic.selection.keyboard.TrainingQuestionKeyboard;

@Component
@RequiredArgsConstructor
public class TrainingQuestionTelegramView {

    private final TelegramMessage telegramMessage;
    private final TrainingQuestionKeyboard keyboard;

    public SendMessage question(Long chatId, TrainingQuestionData question) {
        return question(chatId, question, null);
    }

    public SendMessage question(Long chatId, TrainingQuestionData question, String answerFeedback) {
        StringBuilder text = new StringBuilder();

        if (answerFeedback != null) {
            text.append(answerFeedback)
                .append("\n\n");
        }

        text.append("🎓 Переведите слово:\n\n")
            .append(question.word());

        if (question.transcription() != null && !question.transcription().isBlank()) {
            text.append("\n").append(question.transcription());
        }

        return telegramMessage.withKeyboard(
                chatId,
                text.toString(),
                keyboard.question(question)
        );
    }
}
