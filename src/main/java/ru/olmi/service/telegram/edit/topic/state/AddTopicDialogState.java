package ru.olmi.service.telegram.edit.topic.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DialogState;
import ru.olmi.service.telegram.edit.EditWordSource;
import ru.olmi.service.telegram.state.DialogStateStorage;

@Component
@RequiredArgsConstructor
public class AddTopicDialogState {

    private final DialogStateStorage stateStorage;

    private final Map<Long, AddTopicContext> contexts = new ConcurrentHashMap<>();

    public void start(TelegramUser user, Long userWordId, EditWordSource source) {
        contexts.put(user.getId(), new AddTopicContext(userWordId, source));

        stateStorage.set(user.getId(), DialogState.WAITING_FOR_TOPIC);
    }

    public boolean isWaiting(TelegramUser user) {
        return stateStorage.get(user.getId()) == DialogState.WAITING_FOR_TOPIC;
    }

    public AddTopicContext get(TelegramUser user) {
        return contexts.get(user.getId());
    }

    public void finish(TelegramUser user) {
        stateStorage.clear(user.getId());
        contexts.remove(user.getId());
    }

    public record AddTopicContext(Long userWordId, EditWordSource source) {
    }
}
