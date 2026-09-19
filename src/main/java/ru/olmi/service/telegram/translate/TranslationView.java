package ru.olmi.service.telegram.translate;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.dto.TranslationResult;
import ru.olmi.service.impl.TranslationMessageFormatter;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.translate.keyboard.TranslationKeyboard;

@Component
@RequiredArgsConstructor
public class TranslationView {
    private final TelegramMessage telegramMessage;
    private final TranslationMessageFormatter translationMessageFormatter;
    private final TranslationKeyboard translationKeyboard;

    public SendMessage enterWord(Long chatId) {
        return telegramMessage.text(chatId, "Введите слово:");
    }

    public SendMessage unavailable(Long chatId) {
        return telegramMessage.text(chatId, "Слово больше недоступно.");
    }

    public SendMessage result(Long chatId, TranslationResult result) {
        return telegramMessage.withKeyboard(chatId, translationMessageFormatter.format(result), translationKeyboard.result(result.getWordId()));
    }

    public SendMessage notFound(Long chatId) {
        return telegramMessage.text(chatId, "Не удалось найти перевод.");
    }

    public SendMessage results(Long chatId, List<TranslationResult> results) {
        return telegramMessage.withKeyboard(chatId, "Найдено несколько слов. Выберите нужное:", translationKeyboard.results(results)
        );
    }
}
