package ru.olmi.service.telegram.learning.daily;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.UserLearningWord;
import ru.olmi.dto.TrainingQuestionData;
import ru.olmi.service.impl.training.TrainingService;
import ru.olmi.service.impl.training.TrainingSessionService;
import ru.olmi.service.storage.UserLearningWordStorage;
import ru.olmi.service.telegram.learning.daily.view.DailyTrainingView;
import ru.olmi.service.telegram.learning.training.view.TrainingQuestionTelegramView;

@Component
@RequiredArgsConstructor
public class DailyTrainingService {

    private static final int MAX_WORDS_PER_SESSION = 10;
    private static final int MIN_WORDS_PER_SESSION = 4;

    private final UserLearningWordStorage userLearningWordStorage;
    private final TrainingSessionService trainingSessionService;
    private final TrainingService trainingService;
    private final TrainingQuestionTelegramView trainingQuestionView;
    private final DailyTrainingView view;

    public SendMessage start(TelegramUser user, Long chatId) {
        List<UserLearningWord> words = userLearningWordStorage.findReadyForToday(
                user,
                Instant.now(),
                MAX_WORDS_PER_SESSION
        );

        if (words.size() < MIN_WORDS_PER_SESSION) {
            return view.notEnoughWords(chatId);
        }

        TrainingSession session = trainingSessionService.createDaily(user, words);

        TrainingQuestionData question = trainingService.nextQuestion(user, session.getId());

        return trainingQuestionView.question(chatId, question);
    }
}
