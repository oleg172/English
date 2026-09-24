package ru.olmi.service.telegram.learning.daily.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class DailyTrainingView {

    private final TelegramMessage telegramMessage;

    public SendMessage notEnoughWords(Long chatId) {
        return telegramMessage.text(chatId, "📅 На сегодня пока недостаточно готовых слов для обучения.");
    }
}
