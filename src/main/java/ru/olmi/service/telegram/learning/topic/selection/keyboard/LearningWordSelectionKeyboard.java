package ru.olmi.service.telegram.learning.topic.selection.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.olmi.domain.UserWord;
import ru.olmi.service.telegram.learning.topic.selection.callback.LearningWordSelectionCallback;

@Component
public class LearningWordSelectionKeyboard {

    public InlineKeyboardMarkup words(List<UserWord> words, Set<Long> selectedWordIds, int page, int totalPages
    ) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (UserWord userWord : words) {
            boolean selected = selectedWordIds.contains(userWord.getId());

            String title = (selected ? "☑️ " : "⬜ ") + userWord.getWord().getWord();

            keyboard.add(
                    List.of(button(title, LearningWordSelectionCallback.word(userWord.getId())))
            );
        }

        keyboard.add(navigation(page, totalPages));

        keyboard.add(
                List.of(button("Выбрано: " + selectedWordIds.size(), LearningWordSelectionCallback.current()))
        );

        if (selectedWordIds.size() > 3) {
            keyboard.add(
                    List.of(button("▶️ Начать обучение", LearningWordSelectionCallback.confirm()))
            );
        }

        keyboard.add(
                List.of(button("↩️ К топикам", LearningWordSelectionCallback.back()))
        );

        return InlineKeyboardMarkup.builder()
                                   .keyboard(keyboard)
                                   .build();
    }

    private List<InlineKeyboardButton> navigation(int page, int totalPages) {
        List<InlineKeyboardButton> navigation = new ArrayList<>();

        if (page > 0) {
            navigation.add(button("⬅️", LearningWordSelectionCallback.previous()));
        }

        navigation.add(button((page + 1) + "/" + totalPages, LearningWordSelectionCallback.current()));

        if (page + 1 < totalPages) {
            navigation.add(button("➡️", LearningWordSelectionCallback.next()));
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
