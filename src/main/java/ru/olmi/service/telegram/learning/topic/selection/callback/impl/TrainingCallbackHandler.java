package ru.olmi.service.telegram.learning.topic.selection.callback.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.olmi.domain.TelegramUser;
import ru.olmi.dto.TrainingAnswerResult;
import ru.olmi.service.impl.training.TrainingService;
import ru.olmi.service.telegram.handler.callback.TelegramCallbackHandlerDelegate;
import ru.olmi.service.telegram.learning.menu.LearningMenuView;
import ru.olmi.service.telegram.learning.topic.selection.callback.TrainingCallback;
import ru.olmi.service.telegram.learning.topic.selection.view.TrainingQuestionTelegramView;
import ru.olmi.service.telegram.learning.topic.selection.view.TrainingResultTelegramView;

@Component
@RequiredArgsConstructor
public class TrainingCallbackHandler implements TelegramCallbackHandlerDelegate {

    private final TrainingService trainingService;
    private final TrainingQuestionTelegramView trainingQuestionView;
    private final TrainingResultTelegramView trainingResultView;
    private final LearningMenuView learningMenuView;

    @Override
    public boolean supports(CallbackQuery callbackQuery) {
        return TrainingCallback.isAnswer(callbackQuery.getData())
                || TrainingCallback.isMenu(callbackQuery.getData());
    }

    @Override
    public void handle(CallbackQuery callbackQuery, TelegramUser user, Consumer<SendMessage> sender) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        if (TrainingCallback.isMenu(data)) {
            sender.accept(learningMenuView.show(chatId));
            return;
        } else {

            Long sessionWordId = TrainingCallback.getSessionWordId(data);

            int answerIndex = TrainingCallback.getAnswerIndex(data);

            TrainingAnswerResult result = trainingService.answer(user, sessionWordId, answerIndex);

            if (result.completed()) {
                sender.accept(trainingResultView.completed(chatId, result));
                return;
            }

            String feedback = result.correct()
                              ? "✅ Правильно!"
                              : """
                                      ❌ Неправильно.

                                      Ваш ответ: %s
                                      Правильный ответ: %s
                                      """.formatted(
                                      result.selectedAnswer(),
                                      result.correctAnswer()
                              );
            sender.accept(trainingQuestionView.question(chatId, result.nextQuestion(), feedback));
        }
    }
}
