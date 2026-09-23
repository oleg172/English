package ru.olmi.service.telegram.learning.menu;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.learning.menu.callback.LearningMenuCallback;

@Component
public class LearningMenuKeyboard {

    public InlineKeyboardMarkup create(LearningMenu menu) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(
                menu.items()
                    .stream()
                    .map(this::createRow)
                    .toList()
        );

        keyboard.add(
                List.of(
                        InlineKeyboardButton.builder()
                                            .text("↩️ Назад")
                                            .callbackData(LearningMenuCallback.MENU)
                                            .build()
                )
        );

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> createRow(LearningMenu.Item item) {
        return List.of(
                InlineKeyboardButton.builder()
                                    .text(item.title())
                                    .callbackData(callbackData(item.action()))
                                    .build()
        );
    }

    private String callbackData(LearningMenu.Action action) {
        return switch (action) {
            case TOPIC -> LearningMenuCallback.TOPIC;
        };
    }
}
