package ru.olmi.service.impl.training;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.TrainingSessionWord;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.domain.Word;
import ru.olmi.domain.enums.TrainingSessionStatus;
import ru.olmi.dto.TrainingAnswerResult;
import ru.olmi.dto.TrainingMistake;
import ru.olmi.dto.TrainingQuestionData;
import ru.olmi.repository.TrainingSessionRepository;
import ru.olmi.repository.TrainingSessionWordRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainingSessionWordRepository trainingSessionWordRepository;

    public TrainingQuestionData nextQuestion(TelegramUser user, Long sessionId) {
        TrainingSession session = findSession(user, sessionId);

        if (session.getStatus() == TrainingSessionStatus.CREATED) {
            session.setStatus(TrainingSessionStatus.IN_PROGRESS);
        }

        return trainingSessionWordRepository
                .findAllBySessionIdAndUserId(session.getId(), user.getId())
                .stream()
                .filter(word -> !word.isAnswered())
                .findFirst()
                .map(this::toQuestion)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No unanswered words in training session: " + sessionId
                        )
                );
    }

    public TrainingAnswerResult answer(TelegramUser user, Long sessionWordId, int answerIndex) {
        TrainingSessionWord sessionWord = trainingSessionWordRepository.findByIdAndUserId(sessionWordId, user.getId())
                                                                       .orElseThrow(() -> new IllegalArgumentException(
                                                                               "Training session word not found: " + sessionWordId));

        if (sessionWord.isAnswered()) {
            throw new IllegalStateException("Training session word is already answered: " + sessionWordId);
        }

        List<String> answers = sessionWord.getAnswers();

        if (answerIndex < 0 || answerIndex >= answers.size()) {
            throw new IllegalArgumentException("Invalid answer index: " + answerIndex);
        }

        String selectedAnswer = answers.get(answerIndex);
        boolean correct = selectedAnswer.equals(sessionWord.getCorrectAnswer());

        UserLearningWord learningWord = sessionWord.getUserLearningWord();
        Instant now = Instant.now();
        if (correct) {
            learningWord.correctAnswer(now);
        } else {
            learningWord.incorrectAnswer(now);
        }

        sessionWord.setAnswered(true)
                   .setSelectedAnswer(selectedAnswer)
                   .setAnsweredAt(Instant.now());

        TrainingSession session = sessionWord.getTrainingSession();

        session.setAnsweredWords(session.getAnsweredWords() + 1);

        if (correct) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
        } else {
            session.setIncorrectAnswers(session.getIncorrectAnswers() + 1);
        }

        if (session.getAnsweredWords() == session.getTotalWords()) {
            session.setStatus(TrainingSessionStatus.COMPLETED)
                   .setFinishedAt(Instant.now());

            List<TrainingMistake> mistakes = trainingSessionWordRepository.findAllBySessionIdAndUserId(session.getId(), user.getId()).stream()
                                                                          .filter(trainingSessionWord -> !trainingSessionWord.getSelectedAnswer()
                                                                                                                             .equals(trainingSessionWord.getCorrectAnswer()))
                                                                          .map(trainingSessionWord -> {
                                                                              Word word = trainingSessionWord.getUserLearningWord()
                                                                                                             .getUserWord()
                                                                                                             .getWord();
                                                                              return new TrainingMistake(
                                                                                      word.getWord(),
                                                                                      trainingSessionWord.getSelectedAnswer(),
                                                                                      trainingSessionWord.getCorrectAnswer());
                                                                          })
                                                                          .toList();

            return new TrainingAnswerResult(
                    correct,
                    true,
                    selectedAnswer,
                    sessionWord.getCorrectAnswer(),
                    session.getTotalWords(),
                    session.getCorrectAnswers(),
                    session.getIncorrectAnswers(),
                    null,
                    mistakes
            );
        }

        return new TrainingAnswerResult(
                correct,
                false,
                selectedAnswer,
                sessionWord.getCorrectAnswer(),
                session.getTotalWords(),
                session.getCorrectAnswers(),
                session.getIncorrectAnswers(),
                nextQuestion(user, session.getId()),
                List.of()
        );
    }

    private TrainingSession findSession(TelegramUser user, Long sessionId) {
        return trainingSessionRepository
                .findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Training session not found: " + sessionId));
    }

    private TrainingQuestionData toQuestion(TrainingSessionWord sessionWord) {
        Word word = sessionWord
                .getUserLearningWord()
                .getUserWord()
                .getWord();

        return new TrainingQuestionData(
                sessionWord.getId(),
                word.getWord(),
                word.getTranscription(),
                sessionWord.getAnswers()
        );
    }
}
