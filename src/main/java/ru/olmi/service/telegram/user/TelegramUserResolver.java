package ru.olmi.service.telegram.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.TelegramUserData;
import ru.olmi.service.storage.TelegramUserService;

@Component
@RequiredArgsConstructor
public class TelegramUserResolver {

    private final TelegramUserService userService;

    public TelegramUser resolve(User telegramUser) {
        return userService.findOrCreate(
                new TelegramUserData(
                        telegramUser.getId(),
                        telegramUser.getUserName(),
                        telegramUser.getFirstName(),
                        telegramUser.getLastName(),
                        telegramUser.getLanguageCode()
                )
        );
    }
}
