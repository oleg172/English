package ru.olmi.service.telegram.handler.callback;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.user.TelegramUserResolver;

@Component
@RequiredArgsConstructor
public class TelegramCallbackHandler {

    private final TelegramUserResolver userResolver;
    private final List<TelegramCallbackHandlerDelegate> handlers;
    private final List<TelegramCallbackHandlerWithEditDelegate> editHandlers;

    public void handle(CallbackQuery callbackQuery, Consumer<SendMessage> sender, Consumer<EditMessageText> editor) {
        TelegramUser user = userResolver.resolve(callbackQuery.getFrom());

        editHandlers.stream()
                    .filter(handler -> handler.supports(callbackQuery))
                    .findFirst()
                    .ifPresentOrElse(
                            handler -> handler.handle(callbackQuery, user, sender, editor),
                            () -> handlers.stream()
                                          .filter(handler -> handler.supports(callbackQuery))
                                          .findFirst()
                                          .ifPresent(handler ->
                                                  handler.handle(callbackQuery, user, sender)
                                          )
                    );
    }
}