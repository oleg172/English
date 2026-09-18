package ru.olmi.service.telegram.menu.main.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.menu.main.MainMenu;
import ru.olmi.service.telegram.menu.main.callback.MainMenuCallback;

@Component
public class MainMenuKeyboard {

    public InlineKeyboardMarkup create(MainMenu menu) {
        List<List<InlineKeyboardButton>> keyboard = menu.items()
                                                        .stream()
                                                        .map(this::createRow)
                                                        .toList();

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> createRow(MainMenu.Item item) {
        return List.of(
                InlineKeyboardButton.builder()
                                    .text(item.title())
                                    .callbackData(callbackData(item.action()))
                                    .build()
        );
    }

    private String callbackData(MainMenu.Action action) {
        return switch (action) {
            case TRANSLATE -> MainMenuCallback.TRANSLATE;
            case EDIT_WORD -> MainMenuCallback.EDIT_WORD;
            case MY_WORDS -> MainMenuCallback.MY_WORDS;
        };
    }
}
