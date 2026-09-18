package ru.olmi.service.telegram.dictionary.allwords;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.dictionary.allwords.keyboard.UserDictionaryKeyboard;
import ru.olmi.service.telegram.menu.dictionary.keyboard.UserDictionaryMenuKeyboard;

@Component
@RequiredArgsConstructor
public class UserDictionaryView {
    private final TelegramMessage telegramMessage;
    private final UserDictionaryKeyboard dictionaryKeyboard;
    private final UserDictionaryMenuKeyboard dictionaryMenuKeyboard;

    public SendMessage menu(Long chatId) {
        return telegramMessage.withKeyboard(chatId, "📚 Мой словарь", dictionaryMenuKeyboard.create());
    }

    public SendMessage page(Long chatId, Page<UserWord> result) {
        return telegramMessage.withKeyboard(chatId, "📚 Мой словарь",
                dictionaryKeyboard.create(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage empty(Long chatId) {
        return telegramMessage.text(chatId, "Ваш словарь пока пуст.");
    }
}
