package ru.olmi.service.telegram.dictionary.topic;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;
import ru.olmi.service.telegram.dictionary.topic.search.topic.callback.UserDictionaryTopicSearchCallback;
import ru.olmi.service.telegram.dictionary.topic.search.word.UserDictionaryTopicWordSearchCallback;

@Component
public class UserDictionaryTopicKeyboard {

    public InlineKeyboardMarkup topics(List<Topic> topics, int page, int totalPages) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Topic topic : topics) {
            keyboard.add(
                    List.of(button(topic.getName(), UserDictionaryTopicCallback.topic(topic.getId())))
            );
        }

        keyboard.add(List.of(button("🔎 Найти топик", UserDictionaryTopicSearchCallback.search())));

        keyboard.add(navigation(page, totalPages, UserDictionaryTopicCallback.previous(), UserDictionaryTopicCallback.current(),
                UserDictionaryTopicCallback.next()));

        keyboard.add(List.of(button("↩️ Назад", UserDictionaryTopicCallback.menu())));

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    public InlineKeyboardMarkup words(List<UserWord> words, int page, int totalPages) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (UserWord userWord : words) {
            keyboard.add(
                    List.of(button(userWord.getWord().getWord(), UserDictionaryTopicCallback.word(userWord.getId())))
            );
        }

        keyboard.add(
                List.of(button("🔎 Найти слово", UserDictionaryTopicWordSearchCallback.search()))
        );

        keyboard.add(
                navigation(page, totalPages, UserDictionaryTopicCallback.wordPrevious(), UserDictionaryTopicCallback.wordCurrent(),
                        UserDictionaryTopicCallback.wordNext())
        );

        keyboard.add(List.of(button("↩️ К топикам", UserDictionaryTopicCallback.topics())));

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> navigation(int page, int totalPages, String previous, String current, String next) {
        List<InlineKeyboardButton> navigation = new ArrayList<>();

        if (page > 0) {
            navigation.add(button("⬅️", previous));
        }

        navigation.add(button((page + 1) + "/" + totalPages, current));

        if (page + 1 < totalPages) {
            navigation.add(button("➡️", next));
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
