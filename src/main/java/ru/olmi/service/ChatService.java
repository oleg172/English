package ru.olmi.service;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface ChatService {

    Message sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard, DefaultAbsSender context);

    void editMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard, DefaultAbsSender context);

    void sendTypingAction(Long chatId, DefaultAbsSender context);

    Message sendTemporaryMessage(Long chatId, String text, long delayMillis, InlineKeyboardMarkup keyboard, DefaultAbsSender context);
}
