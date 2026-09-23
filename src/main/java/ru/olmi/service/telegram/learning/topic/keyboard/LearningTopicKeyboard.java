package ru.olmi.service.telegram.learning.topic.keyboard;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.Topic;
import ru.olmi.service.telegram.learning.topic.callback.LearningTopicCallback;

@Component
public class LearningTopicKeyboard {

    public InlineKeyboardMarkup topics(
            List<Topic> topics,
            int page,
            int totalPages
    ) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Topic topic : topics) {
            keyboard.add(
                    List.of(button(topic.getName(), LearningTopicCallback.topic(topic.getId())))
            );
        }

        keyboard.add(
                navigation(page, totalPages)
        );

        keyboard.add(
                List.of(button("↩️ Назад", LearningTopicCallback.menu()))
        );

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> navigation(int page, int totalPages) {
        List<InlineKeyboardButton> navigation = new ArrayList<>();

        if (page > 0) {
            navigation.add(button("⬅️", LearningTopicCallback.previous()));
        }

        navigation.add(button((page + 1) + "/" + totalPages, LearningTopicCallback.current()));

        if (page + 1 < totalPages) {
            navigation.add(button("➡️", LearningTopicCallback.next()));
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