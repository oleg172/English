package ru.olmi.service.telegram.learning.training.keyboard;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.service.telegram.learning.training.callback.TrainingCallback;

@Component
public class TrainingResultKeyboard {

    public InlineKeyboardMarkup completed() {
        return InlineKeyboardMarkup.builder()
                                   .keyboard(List.of(
                                           List.of(InlineKeyboardButton.builder()
                                                                       .text("↩️ К изучению слов")
                                                                       .callbackData(TrainingCallback.menu())
                                                                       .build()
                                           )
                                   ))
                                   .build();
    }
}
