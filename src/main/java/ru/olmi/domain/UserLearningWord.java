package ru.olmi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.olmi.domain.enums.UserLearningWordStatus;

@Entity
@Table(
        name = "user_learning_words",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_learning_words_user_word",
                columnNames = "user_word_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserLearningWord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Слово пользователя, для которого хранится состояние обучения.
     * Одно пользовательское слово может иметь только одну запись обучения.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_word_id", nullable = false, unique = true)
    private UserWord userWord;

    /**
     * Текущее состояние слова в процессе обучения.
     * NEW — слово ещё не изучалось,
     * LEARNING — слово находится в процессе изучения,
     * REVIEW — слово перешло в повторение.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserLearningWordStatus status;

    /**
     * Общее количество правильных ответов за всё время обучения слова.
     */
    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    /**
     * Общее количество неправильных ответов за всё время обучения слова.
     */
    @Column(name = "incorrect_answers", nullable = false)
    private int incorrectAnswers;

    /**
     * Количество правильных ответов подряд в текущей серии обучения.
     * Используется для определения прогресса слова в текущем цикле обучения.
     */
    @Column(name = "consecutive_correct_answers", nullable = false)
    private int consecutiveCorrectAnswers;

    /**
     * Текущий интервал до следующего повторения слова в секундах.
     * Поле подготовлено для будущего SRS.
     */
    @Column(name = "interval_seconds", nullable = false)
    private long intervalSeconds;

    /**
     * Коэффициент лёгкости слова, используемый будущим алгоритмом SRS
     * для расчёта следующего интервала повторения.
     */
    @Column(name = "ease_factor", nullable = false, precision = 4, scale = 2)
    private BigDecimal easeFactor;

    /**
     * Время последнего ответа пользователя по этому слову.
     * Поле подготовлено для будущего SRS.
     */
    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;

    /**
     * Запланированное время следующего повторения слова.
     * Поле подготовлено для будущего SRS.
     */
    @Column(name = "next_review_at")
    private Instant nextReviewAt;
}
