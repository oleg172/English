package ru.olmi.service.telegram.dictionary.topic.search.word;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;

@Component
public class UserDictionaryTopicWordSearchKeyboard {

    public InlineKeyboardMarkup results(List<UserWord> words, int page, int totalPages) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (UserWord userWord : words) {
            keyboard.add(
                    List.of(button(userWord.getWord().getWord(), UserDictionaryTopicCallback.word(userWord.getId())))
            );
        }

        keyboard.add(navigation(page, totalPages));

        keyboard.add(
                List.of(button("↩️ К словам", UserDictionaryTopicCallback.wordCurrent()))
        );

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> navigation(int page, int totalPages) {
        List<InlineKeyboardButton> navigation = new ArrayList<>();

        if (page > 0) {
            navigation.add(
                    button("⬅️", UserDictionaryTopicWordSearchCallback.previous())
            );
        }

        navigation.add(
                button((page + 1) + "/" + totalPages, UserDictionaryTopicWordSearchCallback.current())
        );

        if (page + 1 < totalPages) {
            navigation.add(
                    button("➡️", UserDictionaryTopicWordSearchCallback.next())
            );
        }

        return navigation;
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(callbackData)
                                   .build();
    }
}
