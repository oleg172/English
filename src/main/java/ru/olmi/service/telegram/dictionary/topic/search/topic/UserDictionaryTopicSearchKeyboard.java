package ru.olmi.service.telegram.dictionary.topic.search.topic;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.Topic;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;
import ru.olmi.service.telegram.dictionary.topic.search.topic.callback.UserDictionaryTopicSearchCallback;

@Component
public class UserDictionaryTopicSearchKeyboard {

    public InlineKeyboardMarkup results(List<Topic> topics, int page, int totalPages) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Topic topic : topics) {
            keyboard.add(
                    List.of(button(topic.getName(), UserDictionaryTopicCallback.topic(topic.getId())))
            );
        }

        keyboard.add(navigation(page, totalPages));

        keyboard.add(
                List.of(button("↩️ К топикам", UserDictionaryTopicCallback.topics()))
        );

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> navigation(int page, int totalPages) {
        List<InlineKeyboardButton> navigation = new ArrayList<>();

        if (page > 0) {
            navigation.add(
                    button("⬅️", UserDictionaryTopicSearchCallback.previous())
            );
        }

        navigation.add(
                button((page + 1) + "/" + totalPages, UserDictionaryTopicSearchCallback.current())
        );

        if (page + 1 < totalPages) {
            navigation.add(
                    button("➡️", UserDictionaryTopicSearchCallback.next())
            );
        }

        return navigation;
    }

    public InlineKeyboardMarkup empty() {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("↩️ Назад", UserDictionaryTopicCallback.topics()))
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
