package ru.olmi.service.telegram.edit.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.edit.search.EditWordSearchCallback;

/**
 * Клавиатура для редактирования карточки слова
 */
@Component
public class EditWordKeyboard {

    public InlineKeyboardMarkup create(Long userWordId) {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("✏️ Редактировать", EditWordCallback.edit(userWordId, EditWordSource.SEARCH))),
                                           List.of(button("🗑 Удалить из словаря", EditWordCallback.delete(userWordId, EditWordSource.SEARCH))),
                                           List.of(button("↩️ Назад", EditWordSearchCallback.RESULTS))
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
