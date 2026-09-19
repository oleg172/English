package ru.olmi.service.telegram.upload;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.upload.callback.DictionaryImportCallback;

@Component
public class DictionaryImportKeyboard {

    public InlineKeyboardMarkup result() {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(InlineKeyboardButton.builder()
                                                                       .text("↩️ Главное меню")
                                                                       .callbackData(DictionaryImportCallback.MAIN_MENU)
                                                                       .build())))
                                   .build();
    }
}
