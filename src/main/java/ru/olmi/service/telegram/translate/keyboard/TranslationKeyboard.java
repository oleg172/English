package ru.olmi.service.telegram.translate.keyboard;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.dto.TranslationResult;
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
