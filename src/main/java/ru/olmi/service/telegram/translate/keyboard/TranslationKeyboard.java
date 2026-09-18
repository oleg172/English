package ru.olmi.service.telegram.translate.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.translate.callback.TranslationCallback;

@Component
public class TranslationKeyboard {

    public InlineKeyboardMarkup result(Long wordId) {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("➕ Добавить в словарь", TranslationCallback.addToDictionary(wordId))),
                                           List.of(button("↩️ Главное меню", TranslationCallback.MAIN_MENU))
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
