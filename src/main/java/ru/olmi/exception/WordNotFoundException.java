package ru.olmi.exception;

import ru.olmi.domain.TelegramUser;

public class WordNotFoundException extends TelegramBotException {

    public WordNotFoundException(String word, Long chatId, TelegramUser user) {
        super("Word not found: " + word, chatId, user);
    }
}
