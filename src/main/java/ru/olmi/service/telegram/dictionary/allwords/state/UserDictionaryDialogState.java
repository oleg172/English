package ru.olmi.service.telegram.dictionary.allwords.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;

@Component
@RequiredArgsConstructor
public class UserDictionaryDialogState {

    private final Map<Long, Integer> pages = new ConcurrentHashMap<>();

    public void start(TelegramUser user) {
        pages.put(user.getId(), 0);
    }

    public int getPage(TelegramUser user) {
        return pages.getOrDefault(user.getId(), 0);
    }

    public void setPage(TelegramUser user, int page) {
        pages.put(user.getId(), page);
    }

    public void finish(TelegramUser user) {
        pages.remove(user.getId());
    }
}
