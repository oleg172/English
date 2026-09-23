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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.olmi.domain.enums.TrainingMode;
import ru.olmi.domain.enums.TrainingSessionStatus;

@Entity
@Table(name = "training_sessions")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TrainingSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, которому принадлежит тренировочная сессия.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private TelegramUser user;

    /**
     * Время создания и начала тренировочной сессии.
     */
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    /**
     * Время завершения или отмены тренировочной сессии.
     * NULL означает, что сессия ещё находится в процессе.
     */
    @Column(name = "finished_at")
    private Instant finishedAt;

    /**
     * Текущее состояние тренировочной сессии:
     * CREATED — сессия создана, но обучение ещё не началось;
     * IN_PROGRESS — пользователь отвечает на вопросы;
     * COMPLETED — все слова пройдены;
     * CANCELLED — пользователь прекратил обучение.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TrainingSessionStatus status;

    /**
     * Сценарий, по которому была создана тренировочная сессия.
     * Сейчас используется TOPIC; другие режимы могут появиться в будущем.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 20)
    private TrainingMode mode;

    /**
     * Общее количество слов, выбранных для этой тренировочной сессии.
     */
    @Column(name = "total_words", nullable = false)
    private int totalWords;

    /**
     * Количество слов, на которые пользователь уже ответил.
     */
    @Column(name = "answered_words", nullable = false)
    private int answeredWords;

    /**
     * Количество правильных ответов в рамках этой сессии.
     */
    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    /**
     * Количество неправильных ответов в рамках этой сессии.
     */
    @Column(name = "incorrect_answers", nullable = false)
    private int incorrectAnswers;
}
