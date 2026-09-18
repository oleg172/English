package ru.olmi.service.telegram.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import ru.olmi.dto.DialogState;

@Component
public class DialogStateStorage {

    private final Map<Long, DialogState> states = new ConcurrentHashMap<>();

    public DialogState get(Long userId) {
        return states.getOrDefault(userId, DialogState.IDLE);
    }

    public void set(Long userId, DialogState state) {
        states.put(userId, state);
    }

    public void clear(Long userId) {
        states.remove(userId);
    }
}