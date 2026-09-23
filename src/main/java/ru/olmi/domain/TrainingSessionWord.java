package ru.olmi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.olmi.domain.serializers.StringListConverter;

@Entity
@Table(
        name = "training_session_words",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_training_session_words_session_word",
                        columnNames = {"training_session_id", "user_learning_word_id"}
                ),
                @UniqueConstraint(
                        name = "uq_training_session_words_session_position",
                        columnNames = {"training_session_id", "position"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TrainingSessionWord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тренировочная сессия, в рамках которой пользователь изучает слово.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_session_id", nullable = false)
    private TrainingSession trainingSession;

    /**
     * Состояние обучения слова, которое было включено в эту тренировочную сессию.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_learning_word_id", nullable = false)
    private UserLearningWord userLearningWord;

    /**
     * Порядковый номер слова в тренировочной сессии.
     * Позволяет восстановить порядок вопросов и продолжить сессию после перезапуска приложения.
     */
    @Column(name = "position", nullable = false)
    private int position;

    /**
     * Показывался ли пользователю результат по этому слову.
     * FALSE означает, что пользователь ещё не ответил на вопрос.
     */
    @Column(name = "answered", nullable = false)
    private boolean answered;

    /**
     * Перевод, который пользователь выбрал в качестве ответа.
     * NULL, пока пользователь не ответил.
     */
    @Column(name = "selected_answer", length = 1000)
    private String selectedAnswer;

    /**
     * Правильный перевод, который был выбран для этого вопроса.
     * Сохраняется как снимок на момент создания вопроса.
     */
    @Column(name = "correct_answer", nullable = false, length = 1000)
    private String correctAnswer;

    /**
     * Варианты ответа для предложенного слова
     */
    @Convert(converter = StringListConverter.class)
    @Column(name = "answers", nullable = false, length = 4000)
    private List<String> answers;

    /**
     * Время, когда пользователь ответил на вопрос.
     * NULL, пока пользователь не ответил.
     */
    @Column(name = "answered_at")
    private Instant answeredAt;
}