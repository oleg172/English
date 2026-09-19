package ru.olmi.service.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.olmi.config.AppConfiguration;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.TelegramUserData;
import ru.olmi.repository.TelegramUserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class TelegramUserService {

    private final TelegramUserRepository userRepository;
    private final AppConfiguration configuration;

    public TelegramUser findOrCreate(TelegramUserData data) {
        return userRepository.findByTelegramId(data.telegramId())
                             .map(user -> update(user, data))
                             .orElseGet(() -> create(data));
    }

    private TelegramUser update(TelegramUser user, TelegramUserData data) {
        user.setUsername(data.username());
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setLanguageCode(data.languageCode());

        return userRepository.save(user);
    }

    private TelegramUser create(TelegramUserData data) {
        return userRepository.save(new TelegramUser()
                .setTelegramId(data.telegramId())
                .setUsername(data.username())
                .setFirstName(data.firstName())
                .setLastName(data.lastName())
                .setLanguageCode(data.languageCode())
                .setPreferredToLanguage(configuration.getTranslation().getDefaultToLang())
                .setPreferredFromLanguage(configuration.getTranslation().getDefaultFromLang())
        );
    }
}
