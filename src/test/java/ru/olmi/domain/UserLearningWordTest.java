package ru.olmi.domain;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.olmi.domain.enums.UserLearningWordStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserLearningWordTest {

    private static final Instant START = Instant.parse("2026-01-01T10:00:00Z");

    private UserLearningWord word;

    @BeforeEach
    void setUp() {
        word = new UserLearningWord()
                .setStatus(UserLearningWordStatus.NEW)
                .setCorrectAnswers(0)
                .setIncorrectAnswers(0)
                .setConsecutiveCorrectAnswers(0)
                .setIntervalSeconds(0)
                .setEaseFactor(BigDecimal.valueOf(2.5));
    }

    @Test
    void shouldStartLearningAfterFirstCorrectAnswer() {
        word.correctAnswer(START);

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.LEARNING);
        assertThat(word.getCorrectAnswers()).isEqualTo(1);
        assertThat(word.getConsecutiveCorrectAnswers()).isZero();
        assertThat(word.getIntervalSeconds()).isEqualTo(60 * 60);
        assertThat(word.getLastReviewedAt()).isEqualTo(START);
        assertThat(word.getNextReviewAt()).isEqualTo(START.plusSeconds(60 * 60));
    }

    @Test
    void shouldStartLearningAfterFirstIncorrectAnswer() {
        word.incorrectAnswer(START);

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.LEARNING);
        assertThat(word.getCorrectAnswers()).isZero();
        assertThat(word.getIncorrectAnswers()).isEqualTo(1);
        assertThat(word.getConsecutiveCorrectAnswers()).isZero();
        assertThat(word.getIntervalSeconds()).isEqualTo(60 * 60);
        assertThat(word.getLastReviewedAt()).isEqualTo(START);
        assertThat(word.getNextReviewAt()).isEqualTo(START.plusSeconds(60 * 60));
    }

    @Test
    void shouldNotAdvanceLearningProgressWhenAnsweredTooEarly() {
        word.correctAnswer(START);

        Instant tooEarly = START.plusSeconds(30 * 60);

        word.correctAnswer(tooEarly);
        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.LEARNING);
        assertThat(word.getCorrectAnswers()).isEqualTo(2);
        assertThat(word.getConsecutiveCorrectAnswers()).isZero();
        assertThat(word.getIntervalSeconds()).isEqualTo(60 * 60);
        assertThat(word.getNextReviewAt()).isEqualTo(START.plusSeconds(60 * 60));
        assertThat(word.getLastReviewedAt()).isEqualTo(tooEarly);
    }

    @Test
    void shouldAdvanceToOneDayAfterSuccessfulOneHourInterval() {
        word.correctAnswer(START);

        Instant nextReview = START.plusSeconds(60 * 60);

        word.correctAnswer(nextReview);

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.LEARNING);
        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(1);
        assertThat(word.getIntervalSeconds()).isEqualTo(24 * 60 * 60);
        assertThat(word.getNextReviewAt()).isEqualTo(nextReview.plusSeconds(24 * 60 * 60));
    }

    @Test
    void shouldAdvanceThroughAllLearningIntervals() {
        word.correctAnswer(START);

        Instant oneHourReview =
                START.plusSeconds(60 * 60);

        word.correctAnswer(oneHourReview);

        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(1);

        Instant oneDayReview = oneHourReview.plusSeconds(24 * 60 * 60);

        word.correctAnswer(oneDayReview);

        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(2);
        assertThat(word.getIntervalSeconds()).isEqualTo(2 * 24 * 60 * 60);

        Instant twoDaysReview = oneDayReview.plusSeconds(2 * 24 * 60 * 60);

        word.correctAnswer(twoDaysReview);

        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(3);
        assertThat(word.getIntervalSeconds()).isEqualTo(4 * 24 * 60 * 60);

        Instant fourDaysReview = twoDaysReview.plusSeconds(4 * 24 * 60 * 60);

        word.correctAnswer(fourDaysReview);

        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(4);
        assertThat(word.getIntervalSeconds()).isEqualTo(7 * 24 * 60 * 60);

        Instant sevenDaysReview = fourDaysReview.plusSeconds(7 * 24 * 60 * 60);

        word.correctAnswer(sevenDaysReview);

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.REVIEW);
        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(5);
        assertThat(word.getCorrectAnswers()).isEqualTo(6);
    }

    @Test
    void shouldMoveOneLearningStepBackAfterIncorrectAnswer() {
        word.correctAnswer(START);

        Instant oneHourReview = START.plusSeconds(60 * 60);

        word.correctAnswer(oneHourReview);

        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(1);

        Instant oneDayReview = oneHourReview.plusSeconds(24 * 60 * 60);

        word.correctAnswer(oneDayReview);

        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(2);

        word.incorrectAnswer(oneDayReview.plusSeconds(10));

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.LEARNING);
        assertThat(word.getIncorrectAnswers()).isEqualTo(1);
        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(1);
        assertThat(word.getIntervalSeconds()).isEqualTo(24 * 60 * 60);
    }

    @Test
    void shouldNotMakeReviewWordLearningAfterIncorrectAnswerImmediately() {
        word.setStatus(UserLearningWordStatus.REVIEW)
            .setConsecutiveCorrectAnswers(5)
            .setIntervalSeconds(7 * 24 * 60 * 60);

        word.incorrectAnswer(START);

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.LEARNING);
        assertThat(word.getIncorrectAnswers()).isEqualTo(1);
        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(4);
        assertThat(word.getIntervalSeconds()).isEqualTo(7 * 24 * 60 * 60);
    }

    @Test
    void shouldKeepReviewStatusAfterCorrectAnswer() {
        word.setStatus(UserLearningWordStatus.REVIEW)
            .setConsecutiveCorrectAnswers(5)
            .setIntervalSeconds(7 * 24 * 60 * 60);

        word.correctAnswer(START);

        assertThat(word.getStatus()).isEqualTo(UserLearningWordStatus.REVIEW);
        assertThat(word.getCorrectAnswers()).isEqualTo(1);
        assertThat(word.getConsecutiveCorrectAnswers()).isEqualTo(5);
    }

    @Test
    void shouldKeepCumulativeStatistics() {
        word.correctAnswer(START);
        word.incorrectAnswer(START.plusSeconds(60));

        assertThat(word.getCorrectAnswers()).isEqualTo(1);
        assertThat(word.getIncorrectAnswers()).isEqualTo(1);

        word.correctAnswer(START.plusSeconds(60 * 60));

        assertThat(word.getCorrectAnswers()).isEqualTo(2);
        assertThat(word.getIncorrectAnswers()).isEqualTo(1);
    }
}
