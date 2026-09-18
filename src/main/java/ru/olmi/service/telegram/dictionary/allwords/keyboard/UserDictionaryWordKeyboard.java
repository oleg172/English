package ru.olmi.service.telegram.dictionary.allwords.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.dictionary.allwords.callback.UserDictionaryCallback;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;

@Component
public class UserDictionaryWordKeyboard {

    public InlineKeyboardMarkup create(Long userWordId) {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("✏️ Редактировать", EditWordCallback.edit(userWordId, EditWordSource.DICTIONARY))),
                                           List.of(button("🗑 Удалить из словаря", EditWordCallback.delete(userWordId, EditWordSource.DICTIONARY))),
                                           List.of(button("↩️ Назад", UserDictionaryCallback.BACK))
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
