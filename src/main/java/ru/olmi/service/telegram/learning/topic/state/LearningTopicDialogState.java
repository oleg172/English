package ru.olmi.service.telegram.learning.topic.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DialogState;
import ru.olmi.service.telegram.state.DialogStateStorage;

@Component
@RequiredArgsConstructor
public class LearningTopicDialogState {
    private final DialogStateStorage dialogStateStorage;

    private final Map<Long, Integer> pages = new ConcurrentHashMap<>();

    public void start(TelegramUser user) {
        dialogStateStorage.set(user.getId(), DialogState.WAITING_FOR_LEARNING_TOPIC);

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
        dialogStateStorage.clear(user.getId());
    }
}
