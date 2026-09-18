package ru.olmi.service.telegram.edit.translation.keyboard;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.edit.translation.callback.EditWordTranslationCallback;

/**
 * Клавиатура для удаления пользовательского перевода
 */
@Component
public class DeleteTranslationKeyboard {

    public InlineKeyboardMarkup create(Long userWordId, List<UserWordTranslation> translations) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (UserWordTranslation translation : translations) {
            keyboard.add(List.of(button("🗑 " + translation.getTranslation(), EditWordTranslationCallback.remove(userWordId, translation.getId()))));
        }

        keyboard.add(List.of(button("↩️ Назад", EditWordCallback.editMenu(userWordId))));

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(callbackData)
                                   .build();
    }
}
