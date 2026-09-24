package ru.olmi.service.storage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.enums.UserLearningWordStatus;
import ru.olmi.repository.UserLearningWordRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class UserLearningWordStorage {

    private final UserLearningWordRepository repository;

    public UserLearningWord create(UserWord userWord) {
        return repository.save(
                new UserLearningWord()
                        .setUserWord(userWord)
                        .setStatus(UserLearningWordStatus.NEW)
                        .setCorrectAnswers(0)
                        .setIncorrectAnswers(0)
                        .setConsecutiveCorrectAnswers(0)
                        .setIntervalSeconds(0)
                        .setEaseFactor(BigDecimal.valueOf(2.5))
        );
    }

    @Transactional(readOnly = true)
    public List<UserLearningWord> findReadyForToday(TelegramUser user, Instant now, int limit) {
        return repository.findReadyForToday(
                user.getId(),
                UserLearningWordStatus.NEW,
                now,
                PageRequest.of(0, limit)
        );
    }
}
