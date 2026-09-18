package ru.olmi.exception;

import lombok.Getter;
import ru.olmi.domain.TelegramUser;

// Базовый класс для исключений бота
@Getter
public abstract class TelegramBotException extends RuntimeException {
    private final Long chatId;
    private final TelegramUser user;

    public TelegramBotException(String message, Long chatId, TelegramUser user) {
        super(message);
        this.chatId = chatId;
        this.user = user;
    }
}
