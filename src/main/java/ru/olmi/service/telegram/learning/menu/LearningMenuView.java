package ru.olmi.service.telegram.learning.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.service.telegram.common.TelegramMessage;

@Component
@RequiredArgsConstructor
public class LearningMenuView {

    private final LearningMenuService learningMenuService;
    private final LearningMenuKeyboard learningMenuKeyboard;
    private final TelegramMessage telegramMessage;

    public SendMessage show(Long chatId) {
        LearningMenu menu = learningMenuService.getMenu();

        return telegramMessage.withKeyboard(chatId, menu.text(), learningMenuKeyboard.create(menu));
    }
}
