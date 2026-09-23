package ru.olmi.service.impl.training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.olmi.domain.Translation;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.domain.Word;
import ru.olmi.dto.TrainingQuestion;

@Component
public class TrainingQuestionService {

    public Optional<TrainingQuestion> createQuestion(UserLearningWord learningWord, List<UserLearningWord> candidates) {
        String correctAnswer = findCorrectAnswer(learningWord);

        List<String> distractors = candidates.stream()
                                             .filter(candidate -> !candidate.getId().equals(learningWord.getId()))
                                             .map(this::findCorrectAnswer)
                                             .filter(answer -> !answer.equalsIgnoreCase(correctAnswer))
                                             .distinct()
                                             .limit(3)
                                             .toList();

        if (distractors.size() < 3) {
            return Optional.empty();
        }

        List<String> answers = new ArrayList<>(distractors);
        answers.add(correctAnswer);
        Collections.shuffle(answers);

        UserWord userWord = learningWord.getUserWord();
        Word word = userWord.getWord();

        return Optional.of(new TrainingQuestion(
                learningWord.getId(),
                word.getWord(),
                word.getTranscription(),
                correctAnswer,
                answers
        ));
    }

    private String findCorrectAnswer(UserLearningWord learningWord) {
        UserWord userWord = learningWord.getUserWord();

        if (!userWord.getTranslations().isEmpty()) {
            return userWord.getTranslations().stream()
                           .map(UserWordTranslation::getTranslation)
                           .min(String.CASE_INSENSITIVE_ORDER)
                           .orElseThrow();
        }

        return userWord.getWord().getTranslations().stream()
                       .map(Translation::getTranslation)
                       .min(String.CASE_INSENSITIVE_ORDER)
                       .orElseThrow(() ->
                               new IllegalStateException(
                                       "No translation found for user word: " + userWord.getId()
                               )
                       );
    }
}
