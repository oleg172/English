package ru.olmi.service.telegram.menu.dictionary;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.service.telegram.dictionary.allwords.UserDictionaryView;

@Component
@RequiredArgsConstructor
public class UserDictionaryMenuService {

    private final UserDictionaryView view;

    public SendMessage show(Long chatId) {
        return view.menu(chatId);
    }
}
