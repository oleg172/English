package ru.olmi.service.telegram.learning.menu;

import java.util.List;

public record LearningMenu(
        String text,
        List<Item> items
) {

    public record Item(
            String title,
            Action action
    ) {
    }

    public enum Action {
        TOPIC,
        DAILY
    }
}
