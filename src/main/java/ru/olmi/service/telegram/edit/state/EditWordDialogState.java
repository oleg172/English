package ru.olmi.service.telegram.edit.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.edit.EditWordSource;

@Component
public class EditWordDialogState {

    private final Map<Long, EditContext> contexts = new ConcurrentHashMap<>();

    public void start(TelegramUser user, Long userWordId, EditWordSource source) {
        contexts.put(user.getId(), new EditContext(userWordId, source));
    }

    public EditContext get(TelegramUser user) {
        return contexts.get(user.getId());
    }

    public void finish(TelegramUser user) {
        contexts.remove(user.getId());
    }

    public record EditContext(Long userWordId, EditWordSource source) {
    }
}
