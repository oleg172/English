package ru.olmi.exception;

import ru.olmi.domain.TelegramUser;

public class SessionExpiredException extends TelegramBotException {

    public SessionExpiredException(Long chatId, TelegramUser user) {
        super("Session expired", chatId, user);
    }
}
