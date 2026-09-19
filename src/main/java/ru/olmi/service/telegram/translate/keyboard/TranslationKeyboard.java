package ru.olmi.service.telegram.translate.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.dto.TranslationResult;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.translate.callback.TranslationCallback;

@Component
public class TranslationKeyboard {

    public InlineKeyboardMarkup result(TranslationResult result) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        if (result.getUserWordId() == null) {
            buttons.add(List.of(button("➕ Добавить в словарь", TranslationCallback.addToDictionary(result.getWordId()))));
        } else {
            buttons.add(List.of(button("✏️ Редактировать", EditWordCallback.edit(result.getUserWordId(), EditWordSource.TRANSLATION))));
        }

        buttons.add(List.of(button("↩️ Главное меню", TranslationCallback.MAIN_MENU)));

        return InlineKeyboardMarkup.builder()
                                   .keyboard(buttons)
                                   .build();
    }

    public InlineKeyboardMarkup results(List<TranslationResult> results) {
        List<List<InlineKeyboardButton>> buttons = results.stream()
                                                          .map(result -> List.of(button(result.getWord(), TranslationCallback.select(result.getWordId()))))
                                                          .collect(Collectors.toList());

        buttons.add(List.of(button("↩️ Главное меню", TranslationCallback.MAIN_MENU)));

        return InlineKeyboardMarkup.builder()
                                   .keyboard(buttons)
                                   .build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(callbackData)
                                   .build();
    }
}
