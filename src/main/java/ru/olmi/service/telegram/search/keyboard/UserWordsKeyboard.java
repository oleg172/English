package ru.olmi.service.telegram.search.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.edit.search.EditWordSearchCallback;

@Component
public class UserWordsKeyboard {

    public InlineKeyboardMarkup create(List<UserWord> words, int page, int totalPages) {
        List<List<InlineKeyboardButton>> keyboard = words.stream()
                                                         .map(userWord -> List.of(
                                                                 button(userWord.getWord().getWord(), EditWordCallback.word(userWord.getId()))))
                                                         .collect(Collectors.toCollection(ArrayList::new));

        List<InlineKeyboardButton> pagination = new ArrayList<>();

        if (page > 0) {
            pagination.add(button("⬅️", EditWordSearchCallback.PREVIOUS));
        }

        pagination.add(button((page + 1) + "/" + totalPages, "edit_word:page"));

        if (page + 1 < totalPages) {
            pagination.add(button("➡️", EditWordSearchCallback.NEXT));
        }

        keyboard.add(pagination);

        keyboard.add(List.of(button("↩️ Главное меню", EditWordCallback.MENU)));

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
