package ru.olmi.service.telegram.menu.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.menu.main.keyboard.MainMenuKeyboard;

@Component
@RequiredArgsConstructor
public class MainMenuView {

    private final MainMenuService mainMenuService;
    private final MainMenuKeyboard mainMenuKeyboard;
    private final TelegramMessage telegramMessage;

    public SendMessage show(Long chatId) {
        return show(chatId, null);
    }

    public SendMessage show(Long chatId, String prefix) {
        MainMenu menu = mainMenuService.getMenu();

        String text = (prefix == null ? "" : prefix) + menu.text();

        return telegramMessage.withKeyboard(chatId, text, mainMenuKeyboard.create(menu));
    }
}
