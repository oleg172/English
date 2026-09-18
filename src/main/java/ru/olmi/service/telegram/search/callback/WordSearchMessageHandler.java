package ru.olmi.service.telegram.search.callback;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.handler.message.TelegramMessageHandlerDelegate;
import ru.olmi.service.telegram.search.UserWordSearch;
import ru.olmi.service.telegram.search.state.WordSearchDialogState;

@Component
@RequiredArgsConstructor
public class WordSearchMessageHandler implements TelegramMessageHandlerDelegate {

    private final WordSearchDialogState wordSearchDialog;
    private final UserWordSearch userWordSearch;

    @Override
    public boolean supports(Message message, TelegramUser user) {
        return wordSearchDialog.isWaitingForQuery(user);
    }

    @Override
    public void handle(Message message, TelegramUser user, Consumer<SendMessage> sender) {
        String query = message.getText().trim();

        if (query.isEmpty()) {
            return;
        }

        sender.accept(userWordSearch.search(user, message.getChatId(), query));
    }
}
