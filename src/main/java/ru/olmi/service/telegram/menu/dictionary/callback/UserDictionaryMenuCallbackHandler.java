package ru.olmi.service.telegram.menu.dictionary.callback;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.dictionary.allwords.callback.UserDictionaryCallback;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.menu.dictionary.UserDictionaryMenuService;
import ru.olmi.service.telegram.menu.main.MainMenuView;

@Component
@RequiredArgsConstructor
public class UserDictionaryMenuCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final UserDictionaryMenuService userDictionaryService;
    private final MainMenuView mainMenuView;


    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        return UserDictionaryCallback.MENU.equals(data)
                || UserDictionaryCallback.MAIN_MENU.equals(data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();

        Long chatId = callbackQuery.getMessage().getChatId();

        if (UserDictionaryCallback.MENU.equals(data)) {
            sender.accept(userDictionaryService.show(chatId));
            return;
        }

        if (UserDictionaryCallback.MAIN_MENU.equals(data)) {
            sender.accept(mainMenuView.show(chatId));
        }
    }
}
