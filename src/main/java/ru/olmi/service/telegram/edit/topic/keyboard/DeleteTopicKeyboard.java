package ru.olmi.service.telegram.edit.topic.keyboard;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.UserWordTopic;
import ru.olmi.service.telegram.edit.callback.EditWordCallback;
import ru.olmi.service.telegram.edit.topic.callback.EditWordTopicCallback;

/**
 * Клавиатура для удаления топика
 */
@Component
public class DeleteTopicKeyboard {

    public InlineKeyboardMarkup create(Long userWordId, List<UserWordTopic> topics) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (UserWordTopic userWordTopic : topics) {
            keyboard.add(List.of(button("🗑 " + userWordTopic.getTopic().getName(), EditWordTopicCallback.remove(userWordId, userWordTopic.getId()))));
        }

        keyboard.add(List.of(button("↩️ Назад", EditWordCallback.editMenu(userWordId))));

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
