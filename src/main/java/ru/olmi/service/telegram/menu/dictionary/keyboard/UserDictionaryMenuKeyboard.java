package ru.olmi.service.telegram.menu.dictionary.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.dictionary.allwords.callback.UserDictionaryCallback;
import ru.olmi.service.telegram.dictionary.topic.callback.UserDictionaryTopicCallback;

@Component
public class UserDictionaryMenuKeyboard {

    public InlineKeyboardMarkup create() {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(button("📖 Все слова", UserDictionaryCallback.LIST)),
                                           List.of(button("🏷 По топикам", UserDictionaryTopicCallback.topics())),
                                           List.of(button("↩️ Назад", UserDictionaryCallback.MAIN_MENU))
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