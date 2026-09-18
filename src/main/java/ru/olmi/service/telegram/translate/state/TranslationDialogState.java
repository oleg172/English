package ru.olmi.service.telegram.translate.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.DialogState;
import ru.olmi.service.telegram.state.DialogStateStorage;

@Component
@RequiredArgsConstructor
public class TranslationDialogState {

    private final DialogStateStorage stateStorage;

    public void start(TelegramUser user) {
        stateStorage.set(user.getId(), DialogState.WAITING_FOR_WORD);
    }

    public boolean isWaitingForWord(TelegramUser user) {
        return stateStorage.get(user.getId()) == DialogState.WAITING_FOR_WORD;
    }

    public void finish(TelegramUser user) {
        stateStorage.clear(user.getId());
    }
}
