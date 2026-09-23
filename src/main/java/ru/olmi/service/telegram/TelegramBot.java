package ru.olmi.service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.olmi.config.AppConfiguration;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandler;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandler;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final AppConfiguration properties;
    private final TelegramMessageHandler messageHandler;
    private final TelegramCallbackHandler callbackHandler;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && (update.getMessage().hasText() || update.getMessage().hasDocument())) {
                handleMessage(update.getMessage());
                return;
            }

            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            log.error("Failed to process Telegram update", e);
        }
    }

    private void handleMessage(Message message) {
        messageHandler.handle(message, this::send);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        answerCallback(callbackQuery);
        callbackHandler.handle(callbackQuery, this::send, this::edit);
    }

    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error(
                    "Failed to send Telegram message to chat [{}]",
                    message.getChatId(),
                    e
            );
        }
    }

    private void answerCallback(CallbackQuery callbackQuery) {
        try {
            execute(AnswerCallbackQuery.builder()
                                       .callbackQueryId(callbackQuery.getId())
                                       .build()
            );
        } catch (Exception e) {
            log.error("Failed to answer Telegram callback [{}]", callbackQuery.getId(), e);
        }
    }

    private void edit(EditMessageText message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Failed to edit Telegram message [{}] in chat [{}]", message.getMessageId(), message.getChatId(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getBot().getToken();
    }
}