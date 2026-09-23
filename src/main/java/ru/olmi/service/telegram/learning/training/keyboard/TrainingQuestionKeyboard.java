package ru.olmi.service.telegram.learning.training.keyboard;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.dto.TrainingQuestionData;
import ru.olmi.service.telegram.learning.training.callback.TrainingCallback;

@Component
public class TrainingQuestionKeyboard {

    public InlineKeyboardMarkup question(TrainingQuestionData question) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < question.answers().size(); i++) {
            keyboard.add(
                    List.of(button(question.answers().get(i), TrainingCallback.answer(question.sessionWordId(), i)))
            );
        }

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
