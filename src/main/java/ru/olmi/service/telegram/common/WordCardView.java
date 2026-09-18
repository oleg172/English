package ru.olmi.service.telegram.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.olmi.dto.TranslationResult;
import ru.olmi.service.impl.TranslationMessageFormatter;
import ru.olmi.service.telegram.dictionary.allwords.keyboard.UserDictionaryWordKeyboard;
import ru.olmi.service.telegram.dictionary.topic.UserDictionaryTopicKeyboard;
import ru.olmi.service.telegram.dictionary.topic.UserDictionaryTopicWordKeyboard;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.edit.keyboard.EditWordKeyboard;

@Component
@RequiredArgsConstructor
public class WordCardView {

    private final TelegramMessage telegramMessage;
    private final TranslationMessageFormatter translationMessageFormatter;
    private final EditWordKeyboard editWordKeyboard;
    private final UserDictionaryWordKeyboard dictionaryWordKeyboard;
    private final UserDictionaryTopicWordKeyboard dictionaryTopicKeyboard;

    public SendMessage showForEdit(Long chatId, TranslationResult result, Long userWordId, EditWordSource source) {
        InlineKeyboardMarkup keyboard = switch (source) {
            case SEARCH -> editWordKeyboard.create(userWordId);
            case DICTIONARY -> dictionaryWordKeyboard.create(userWordId);
            case TOPIC -> dictionaryTopicKeyboard.create(userWordId);
        };

        return telegramMessage.withKeyboard(chatId, translationMessageFormatter.format(result), keyboard);
    }

    public SendMessage unavailable(Long chatId) {
        return telegramMessage.text(chatId, "Слово больше недоступно.");
    }
}
