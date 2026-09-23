package ru.olmi.service.telegram.common;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class TelegramMessage {

    public SendMessage text(Long chatId, String text) {
        return SendMessage.builder()
                          .chatId(chatId)
                          .text(text)
                          .build();
    }

    public SendMessage withKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        return SendMessage.builder()
                          .chatId(chatId)
                          .text(text)
                          .replyMarkup(keyboard)
                          .build();
    }

    public EditMessageText edit(Long chatId, Integer messageId, String text) {
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(text)
                              .build();
    }

    public EditMessageText edit(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        return EditMessageText.builder()
                              .chatId(chatId)
                              .messageId(messageId)
                              .text(text)
                              .replyMarkup(keyboard)
                              .build();
    }
}
