package ru.olmi.service.telegram.edit.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.edit.topic.callback.EditWordTopicCallback;
import ru.olmi.service.telegram.edit.translation.callback.EditWordTranslationCallback;

/**
 * Клавиатура для редактирования перевода и топика слова
 */
@Component
public class EditWordMenuKeyboard {

    public InlineKeyboardMarkup create(Long userWordId) {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("➕ Добавить перевод", EditWordTranslationCallback.add(userWordId))),
                                           List.of(button("➖ Удалить перевод", EditWordTranslationCallback.delete(userWordId))),
                                           List.of(button("➕ Добавить топик", EditWordTopicCallback.add(userWordId))),
                                           List.of(button("➖ Удалить топик", EditWordTopicCallback.delete(userWordId))),
                                           List.of(button("↩️ Назад", EditWordCallback.editMenu(userWordId))))
                                   )
                                   .build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(callbackData)
                                   .build();
    }
}
