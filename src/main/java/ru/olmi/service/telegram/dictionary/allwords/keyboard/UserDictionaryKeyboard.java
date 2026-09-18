package ru.olmi.service.telegram.dictionary.allwords.keyboard;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.dictionary.allwords.callback.UserDictionaryCallback;

@Component
public class UserDictionaryKeyboard {

    public InlineKeyboardMarkup create(List<UserWord> words, int page, int totalPages) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (UserWord userWord : words) {
            keyboard.add(List.of(
                    button(userWord.getWord().getWord(), UserDictionaryCallback.word(userWord.getId()))
                    )
            );
        }

        List<InlineKeyboardButton> navigation = new ArrayList<>();

        if (page > 0) {
            navigation.add(button("⬅️", UserDictionaryCallback.previous()));
        }

        navigation.add(button((page + 1) + "/" + totalPages, UserDictionaryCallback.current()));

        if (page + 1 < totalPages) {
            navigation.add(button("➡️", UserDictionaryCallback.next()));
        }

        keyboard.add(navigation);

        keyboard.add(List.of(button("↩️ Назад", UserDictionaryCallback.MENU)));

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
