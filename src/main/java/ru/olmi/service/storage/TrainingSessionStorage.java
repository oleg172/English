package ru.olmi.service.storage;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.TrainingSessionWord;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.domain.enums.TrainingMode;
import ru.olmi.domain.enums.TrainingSessionStatus;
import ru.olmi.repository.TrainingSessionRepository;
import ru.olmi.repository.TrainingSessionWordRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class TrainingSessionStorage {
    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainingSessionWordRepository trainingSessionWordRepository;

    public TrainingSession create(TelegramUser user, TrainingMode mode, List<UserLearningWord> words) {
        TrainingSession session = trainingSessionRepository.save(
                new TrainingSession()
                        .setUser(user)
                        .setStartedAt(Instant.now())
                        .setStatus(TrainingSessionStatus.CREATED)
                        .setMode(mode)
                        .setTotalWords(words.size())
                        .setAnsweredWords(0)
                        .setCorrectAnswers(0)
                        .setIncorrectAnswers(0)
        );

        List<TrainingSessionWord> sessionWords = IntStream.range(0, words.size())
                                                          .mapToObj(index -> new TrainingSessionWord()
                                                                  .setTrainingSession(session)
                                                                  .setUserLearningWord(words.get(index))
                                                                  .setPosition(index)
                                                                  .setAnswered(false)
                                                                  .setCorrectAnswer(null)
                                                                  .setSelectedAnswer(null)
                                                                  .setAnsweredAt(null)
                                                          )
                                                          .toList();

        trainingSessionWordRepository.saveAll(sessionWords);

        return session;
    }
}
