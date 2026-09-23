package ru.olmi.service.telegram.learning.topic.selection.state;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DialogState;
import ru.olmi.service.telegram.state.DialogStateStorage;

@Component
@RequiredArgsConstructor
public class LearningWordSelectionDialogState {

    private final DialogStateStorage dialogStateStorage;

    private final Map<Long, SelectionContext> contexts = new ConcurrentHashMap<>();

    public void start(TelegramUser user, Long topicId) {
        dialogStateStorage.set(user.getId(), DialogState.WAITING_FOR_LEARNING_TOPIC);

        contexts.put(user.getId(), new SelectionContext(topicId, 0, new LinkedHashSet<>()));
    }

    public Long getTopicId(TelegramUser user) {
        return getContext(user).topicId();
    }

    public int getPage(TelegramUser user) {
        return getContext(user).page();
    }

    public void setPage(TelegramUser user, int page) {
        SelectionContext context = getContext(user);

        contexts.put(user.getId(), new SelectionContext(
                context.topicId(),
                page,
                context.selectedWordIds())
        );
    }

    public void toggleWord(TelegramUser user, Long userWordId) {
        SelectionContext context = getContext(user);

        Set<Long> selectedWordIds = new LinkedHashSet<>(context.selectedWordIds());

        if (selectedWordIds.contains(userWordId)) {
            selectedWordIds.remove(userWordId);
        } else {
            selectedWordIds.add(userWordId);
        }

        contexts.put(user.getId(), new SelectionContext(
                context.topicId(),
                context.page(),
                selectedWordIds)
        );
    }

    public Set<Long> getSelectedWordIds(TelegramUser user) {
        return Set.copyOf(getContext(user).selectedWordIds());
    }

    public int getSelectedCount(TelegramUser user) {
        return getContext(user).selectedWordIds().size();
    }

    public void finish(TelegramUser user) {
        contexts.remove(user.getId());
        dialogStateStorage.clear(user.getId());
    }

    private SelectionContext getContext(TelegramUser user) {
        SelectionContext context = contexts.get(user.getId());

        if (context == null) {
            throw new IllegalStateException("Learning word selection is not started for user: " + user.getId());
        }

        return context;
    }

    private record SelectionContext(
            Long topicId,
            int page,
            Set<Long> selectedWordIds
    ) {
    }
}
