package ru.olmi.service.telegram.edit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.service.telegram.common.TelegramMessage;
import ru.olmi.service.telegram.edit.keyboard.EditWordMenuKeyboard;

@Component
@RequiredArgsConstructor
public class EditWordView {
    private final TelegramMessage telegramMessage;
    private final EditWordMenuKeyboard editWordMenuKeyboard;

    public SendMessage editMenu(Long chatId, String word, Long userWordId) {
        return telegramMessage.withKeyboard(chatId, "✏️ Редактирование слова: " + word, editWordMenuKeyboard.create(userWordId));
    }
}
