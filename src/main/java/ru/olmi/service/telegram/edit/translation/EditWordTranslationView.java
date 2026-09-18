package ru.olmi.service.telegram.edit.translation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.edit.keyboard.EditWordMenuKeyboard;
import ru.olmi.service.telegram.edit.translation.keyboard.DeleteTranslationKeyboard;

@Component
@RequiredArgsConstructor
public class EditWordTranslationView {
    private final TelegramMessage telegramMessage;
    private final EditWordMenuKeyboard editWordMenuKeyboard;
    private final DeleteTranslationKeyboard deleteTranslationKeyboard;

    public SendMessage sessionUnavailable(Long chatId) {
        return telegramMessage.text(chatId, "Контекст редактирования больше недоступен.");
    }

    public SendMessage enterTranslation(Long chatId) {
        return telegramMessage.text(chatId, "Введите новый перевод:");
    }

    public SendMessage noTranslations(Long chatId, Long userWordId) {
        return telegramMessage.withKeyboard(chatId, "У слова нет пользовательских переводов.", editWordMenuKeyboard.create(userWordId));
    }

    public SendMessage selectTranslation(Long chatId, Long userWordId, List<UserWordTranslation> translations) {
        return telegramMessage.withKeyboard(chatId, "Выберите перевод для удаления:",
                deleteTranslationKeyboard.create(userWordId, translations));
    }

    public SendMessage unavailable(Long chatId) {
        return telegramMessage.text(chatId, "Перевод больше недоступен.");
    }
}
