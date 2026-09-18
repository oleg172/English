package ru.olmi.service.telegram.dictionary.topic;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;

@Component
public class UserDictionaryTopicWordKeyboard {

    public InlineKeyboardMarkup create(Long userWordId) {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("✏️ Редактировать", EditWordCallback.edit(userWordId, EditWordSource.TOPIC))),
                                           List.of(button("🗑 Удалить из словаря", EditWordCallback.delete(userWordId, EditWordSource.TOPIC))),
                                           List.of(button("↩️ Назад", UserDictionaryTopicCallback.wordCurrent()))
                                   ))
                                   .build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(callbackData)
                                   .build();
    }
}
