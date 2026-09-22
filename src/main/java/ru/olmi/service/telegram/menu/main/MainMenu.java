package ru.olmi.service.telegram.menu.main;

import java.util.List;

public record MainMenu(
        String text,
        List<Item> items
) {

    public record Item(
            String title,
            Action action
    ) {
    }

    public enum Action {
        TRANSLATE,
        EDIT_WORD,
        MY_WORDS,
        UPLOAD,
        EXPORT,
        HELP
    }
}
