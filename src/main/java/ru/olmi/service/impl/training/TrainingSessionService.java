package ru.olmi.service.impl.training;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.TrainingSessionWord;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.enums.TrainingMode;
import ru.olmi.domain.enums.TrainingSessionStatus;
import ru.olmi.dto.TrainingQuestion;
import ru.olmi.repository.TopicRepository;
import ru.olmi.repository.TrainingSessionRepository;
import ru.olmi.repository.TrainingSessionWordRepository;
import ru.olmi.repository.UserLearningWordRepository;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.repository.UserWordTopicRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class TrainingSessionService {

    private static final int MIN_WORDS_PER_SESSION = 4;

    private final TopicRepository topicRepository;
    private final UserWordRepository userWordRepository;
    private final UserWordTopicRepository userWordTopicRepository;
    private final UserLearningWordRepository userLearningWordRepository;
    private final TrainingQuestionService trainingQuestionService;
    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainingSessionWordRepository trainingSessionWordRepository;

    public TrainingSession create(TelegramUser user, Long topicId, List<Long> userWordIds) {
        validateWordCount(userWordIds);

        Topic topic = topicRepository.findByIdAndUserId(topicId, user.getId())
                                     .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));

        List<UserWord> selectedWords = findSelectedWords(user, topic, userWordIds);
        List<UserLearningWord> selectedLearningWords = findLearningWords(user, selectedWords);
        List<UserLearningWord> topicCandidates = findTopicCandidates(user, topic);
        List<UserLearningWord> otherCandidates = findOtherCandidates(user, topicCandidates);

        List<TrainingQuestion> questions = createQuestions(selectedLearningWords, topicCandidates, otherCandidates);

        return createSession(user, TrainingMode.TOPIC, selectedLearningWords, questions);
    }

    public TrainingSession createDaily(TelegramUser user, List<UserLearningWord> selectedLearningWords) {
        validateWordCountObjects(selectedLearningWords);

        List<Long> selectedWordIds = selectedLearningWords.stream()
                                                          .map(UserLearningWord::getUserWord)
                                                          .map(UserWord::getId)
                                                          .toList();
        List<UserLearningWord> managedSelectedLearningWords = findLearningWordsByIds(user, selectedWordIds);
        Set<Long> selectedIds = new HashSet<>(selectedWordIds);

        List<UserWord> otherWords = userWordRepository.findAllForTraining(user.getId(), selectedIds);
        List<UserLearningWord> otherCandidates = findLearningWords(user, otherWords);
        List<TrainingQuestion> questions = createQuestions(managedSelectedLearningWords, managedSelectedLearningWords, otherCandidates);

        return createSession(
                user,
                TrainingMode.DAILY,
                managedSelectedLearningWords,
                questions
        );
    }

    private void validateWordCount(List<Long> userWordIds) {
        if (userWordIds.size() < MIN_WORDS_PER_SESSION) {
            throw new IllegalArgumentException("Training session must contain at least " + MIN_WORDS_PER_SESSION);
        }
    }

    private void validateWordCountObjects(List<UserLearningWord> learningWords) {
        if (learningWords.size() < MIN_WORDS_PER_SESSION) {
            throw new IllegalArgumentException("Training session must contain at least " + MIN_WORDS_PER_SESSION);
        }
    }

    private List<UserWord> findSelectedWords(TelegramUser user, Topic topic, List<Long> userWordIds) {
        List<UserWord> selectedWords = userWordTopicRepository.findWordsByTopicAndIds(user.getId(), topic.getId(), userWordIds);

        if (selectedWords.size() != userWordIds.size()) {
            throw new IllegalArgumentException("Some selected words do not belong to the topic");
        }

        Map<Long, UserWord> wordsById = selectedWords.stream()
                                                     .collect(Collectors.toMap(UserWord::getId, Function.identity()));

        return userWordIds.stream()
                          .map(wordsById::get)
                          .toList();
    }

    private List<UserLearningWord> findLearningWords(TelegramUser user, List<UserWord> userWords) {
        List<Long> userWordIds = userWords.stream()
                                          .map(UserWord::getId)
                                          .toList();

        List<UserLearningWord> learningWords = userLearningWordRepository.findForTraining(user.getId(), userWordIds);

        if (learningWords.size() != userWords.size()) {
            throw new IllegalStateException("Learning state is missing for some user words"
            );
        }

        Map<Long, UserLearningWord> byUserWordId = learningWords.stream()
                                                                .collect(Collectors.toMap(
                                                                        learningWord -> learningWord.getUserWord().getId(), Function.identity()));

        return userWords.stream()
                        .map(userWord -> byUserWordId.get(userWord.getId()))
                        .toList();
    }

    private List<UserLearningWord> findLearningWordsByIds(TelegramUser user, List<Long> userWordIds) {
        List<UserLearningWord> learningWords = userLearningWordRepository.findForTraining(user.getId(), userWordIds);

        if (learningWords.size() != userWordIds.size()) {
            throw new IllegalStateException("Learning state is missing for some user words");
        }

        Map<Long, UserLearningWord> byUserWordId = learningWords.stream()
                                                                .collect(Collectors.toMap(learningWord -> learningWord
                                                                                .getUserWord()
                                                                                .getId(),
                                                                        Function.identity()));

        return userWordIds.stream()
                          .map(byUserWordId::get)
                          .toList();
    }

    private List<UserLearningWord> findTopicCandidates(TelegramUser user, Topic topic) {
        List<UserWord> topicWords = userWordTopicRepository.findAllWordsByTopic(user.getId(), topic.getId());

        return findLearningWords(user, topicWords);
    }

    private List<UserLearningWord> findOtherCandidates(TelegramUser user, List<UserLearningWord> topicCandidates) {
        Set<Long> topicWordIds = topicCandidates.stream()
                                                .map(UserLearningWord::getUserWord)
                                                .map(UserWord::getId)
                                                .collect(Collectors.toSet());

        List<UserWord> otherWords = userWordRepository.findAllForTraining(user.getId(), topicWordIds);

        return findLearningWords(user, otherWords);
    }

    private List<TrainingQuestion> createQuestions(List<UserLearningWord> selectedLearningWords, List<UserLearningWord> topicCandidates,
            List<UserLearningWord> otherCandidates) {
        List<UserLearningWord> candidates = Stream.concat(topicCandidates.stream(), otherCandidates.stream()).toList();

        return selectedLearningWords.stream()
                                    .map(word ->
                                            trainingQuestionService.createQuestion(word, candidates)
                                    )
                                    .flatMap(Optional::stream)
                                    .toList();
    }

    private void saveSessionWords(TrainingSession session, List<TrainingQuestion> questions, List<UserLearningWord> selectedLearningWords
    ) {
        Map<Long, UserLearningWord> learningWordsById = selectedLearningWords.stream()
                                                                             .collect(Collectors.toMap(UserLearningWord::getId, Function.identity()));

        List<TrainingSessionWord> sessionWords = IntStream.range(0, questions.size())
                                                          .mapToObj(index -> {
                                                              TrainingQuestion question = questions.get(index);
                                                              UserLearningWord learningWord = learningWordsById.get(question.userLearningWordId());

                                                              return new TrainingSessionWord()
                                                                      .setTrainingSession(session)
                                                                      .setUserLearningWord(learningWord)
                                                                      .setPosition(index)
                                                                      .setAnswered(false)
                                                                      .setSelectedAnswer(null)
                                                                      .setCorrectAnswer(question.correctAnswer())
                                                                      .setAnswers(question.answers())
                                                                      .setAnsweredAt(null);
                                                          })
                                                          .toList();

        trainingSessionWordRepository.saveAll(sessionWords);
    }

    private TrainingSession createSession(TelegramUser user, TrainingMode mode, List<UserLearningWord> selectedLearningWords,
            List<TrainingQuestion> questions) {
        if (questions.size() != selectedLearningWords.size()) {
            throw new IllegalStateException("Unable to create questions for all selected words");
        }

        TrainingSession session = trainingSessionRepository.save(new TrainingSession()
                .setUser(user)
                .setStartedAt(Instant.now())
                .setStatus(TrainingSessionStatus.CREATED)
                .setMode(mode)
                .setTotalWords(questions.size())
                .setAnsweredWords(0)
                .setCorrectAnswers(0)
                .setIncorrectAnswers(0)
        );

        saveSessionWords(session, questions, selectedLearningWords);

        return session;
    }
}
