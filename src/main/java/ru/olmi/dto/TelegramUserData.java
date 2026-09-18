package ru.olmi.dto;

public record TelegramUserData(
        Long telegramId,
        String username,
        String firstName,
        String lastName,
        String languageCode
) {
}
