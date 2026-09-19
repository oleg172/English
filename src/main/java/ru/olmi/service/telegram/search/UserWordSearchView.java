package ru.olmi.service.telegram.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.search.keyboard.UserWordsKeyboard;

@Component
@RequiredArgsConstructor
public class UserWordSearchView {

    private final TelegramMessage telegramMessage;
    private final UserWordsKeyboard userWordsKeyboard;

    public SendMessage page(Long chatId, String query, Page<UserWord> result) {
        return telegramMessage.withKeyboard(chatId, "📚 Результаты поиска: «" + query + "»",
                userWordsKeyboard.create(result.getContent(), result.getNumber(), result.getTotalPages()));
    }

    public SendMessage empty(Long chatId) {
        return telegramMessage.withKeyboard(chatId, "По вашему запросу ничего не найдено.", userWordsKeyboard.empty());
    }

    public SendMessage enterQuery(Long chatId) {
        return telegramMessage.text(chatId, "Введите слово для поиска:");
    }
}
