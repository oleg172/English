package ru.olmi.exception.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.olmi.domain.TelegramUser;

@Component
@AllArgsConstructor
@Slf4j
public class GlobalTelegramExceptionHandler {

    /*private final UserSessionStorage sessionStorage;
    private final ChatService chatService;
    private final List<ViewHandler> viewHandlers;

    public void handleException(Exception e, Long chatId, TelegramUser user, DefaultAbsSender context) {
        if (e instanceof SessionExpiredException) {
            handleSessionExpired((SessionExpiredException) e, context);
        } else if (e instanceof WordNotFoundException) {
            handleWordNotFound((WordNotFoundException) e, context);
        } else if (e instanceof UnexpectedInputException) {
            handleUnexpectedInput((UnexpectedInputException) e, context);
        } else if (e instanceof TelegramApiException) {
            handleTelegramApiException((TelegramApiException) e, chatId, context);
        } else {
            handleGenericException(e, chatId, user, context);
        }
    }

    private void handleSessionExpired(SessionExpiredException e, DefaultAbsSender context) {
        log.warn("Session expired for user {} in chat {}", e.getUser(), e.getChatId());

        sessionStorage.clearSession(e.getUser().getId());

        String message = "⏰ Your session has expired. Please enter word to translate/edit or send '/help'";
        chatService.sendTemporaryMessage(e.getChatId(), message, 5000, null, context);
    }

    private void handleWordNotFound(WordNotFoundException e, DefaultAbsSender context) {
        log.info("Word not found: {} for user {}", e.getMessage(), e.getUser());

        String message = String.format("🔍 Word \"%s\" not found. Try another word or check spelling.",
                extractWordFromMessage(e.getMessage()));

        chatService.sendTemporaryMessage(e.getChatId(), message, 4000, null, context);
    }

    private void handleUnexpectedInput(UnexpectedInputException e, DefaultAbsSender context) {
        log.debug("Invalid input from user {}: {}", e.getUser(), e.getMessage());
        String msg = """
                🤔 *I see you typed something!*
                                    
                In this menu, please use the buttons
                """;
        chatService.sendTemporaryMessage(e.getChatId(), msg, 4000, null, context);

        var session = e.getSession();
        var chatId = e.getChatId();
        var user = e.getUser();

        switch (session.getType()) {
            case WORD_EDIT -> {
                var message = CanHandle.choose(viewHandlers, ViewType.EDIT_WORD_ACTION).sendView(chatId, null, session.getWord(), user, context);
                sessionStorage.updateMsgId(session, message.getMessageId());
            }
            case WORD_MODIFY -> {
                var message = CanHandle.choose(viewHandlers, ViewType.MODIFY_WORD_ACTION).sendView(chatId, null, session.getWord(), user, context);
                sessionStorage.updateMsgId(session, message.getMessageId());
            }
            case ADD_WORD_TO_DICTIONARY -> {
                var message = CanHandle.choose(viewHandlers, ViewType.SAVE_WORD_ACTION).sendView(chatId, null, session.getWord(), user, context);
                sessionStorage.updateMsgId(session, message.getMessageId());
            }
            default -> {
                log.info("Can't find view for type [{}]", session.getType());
            }
        }
    }

    private void handleGenericException(Exception e, Long chatId, TelegramUser user, DefaultAbsSender context) {
        log.error("Unhandled exception in bot for user {}: {}", user, e.getMessage(), e);

        String userMessage = "😔 Something went wrong. Please try again or use '/help'";
        chatService.sendTemporaryMessage(chatId, userMessage, 5000, null, context);
    }

    // Обработка Telegram API исключений
    private void handleTelegramApiException(TelegramApiException e, Long chatId, DefaultAbsSender context) {
        if (e.getMessage().contains("Too Many Requests")) {
            log.warn("Rate limit exceeded for chat {}", chatId);
            chatService.sendTemporaryMessage(chatId, "⏳ Too many requests. Please wait a moment.", 3000, null, context);
        } else if (e.getMessage().contains("bot was blocked")) {
            log.info("Bot was blocked by user in chat {}", chatId);
        } else {
            log.error("Telegram API error for chat {}: {}", chatId, e.getMessage());
            chatService.sendTemporaryMessage(chatId, "📡 Connection issue. Please try again.", 3000, null, context);
        }
    }

    private String extractWordFromMessage(String message) {
        if (message.contains("Word not found: ")) {
            return message.substring("Word not found: ".length());
        }
        return "unknown";
    }*/

}