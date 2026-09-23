package ru.olmi.service.telegram.learning.topic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.UserWord;
import ru.olmi.dto.TrainingQuestionData;
import ru.olmi.service.impl.training.TrainingService;
import ru.olmi.service.impl.training.TrainingSessionService;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.telegram.learning.topic.selection.LearningWordSelectionService;
import ru.olmi.service.telegram.learning.training.view.TrainingQuestionTelegramView;
import ru.olmi.service.telegram.learning.topic.state.LearningTopicDialogState;
import ru.olmi.service.telegram.learning.topic.view.LearningTopicView;

@Component
@RequiredArgsConstructor
public class LearningTopicService {
    private final TopicStorage topicStorage;
    private final LearningTopicDialogState state;
    private final LearningTopicView view;
    private final LearningWordSelectionService learningWordSelectionService;
    private final TrainingSessionService trainingSessionService;
    private final TrainingService trainingService;
    private final TrainingQuestionTelegramView trainingQuestionView;

    public SendMessage topics(TelegramUser user, Long chatId) {
        state.start(user);

        return topicsPage(user, chatId, 0);
    }

    public SendMessage nextTopics(TelegramUser user, Long chatId) {
        return topicsPage(user, chatId, state.getPage(user) + 1);
    }

    public SendMessage previousTopics(TelegramUser user, Long chatId) {
        return topicsPage(user, chatId, state.getPage(user) - 1);
    }

    public SendMessage currentTopics(TelegramUser user, Long chatId) {
        return topicsPage(user, chatId, state.getPage(user));
    }

    private SendMessage topicsPage(TelegramUser user, Long chatId, int page) {
        if (page < 0) {
            page = 0;
        }

        Page<Topic> result = topicStorage.findTopicsForUser(user, page);

        if (result.isEmpty() && page > 0) {
            return topicsPage(user, chatId, page - 1);
        }

        if (result.isEmpty()) {
            return view.emptyTopics(chatId);
        }

        state.setPage(user, result.getNumber());

        return view.topics(chatId, result);
    }

    public SendMessage selectTopic(TelegramUser user, Long chatId, Long topicId) {
        if (topicStorage.findForUser(user, topicId).isEmpty()) {
            return view.emptyTopics(chatId);
        }

        Page<UserWord> words = topicStorage.findWordsByTopic(user, topicId, 0);
        long wordCount = words.getTotalElements();

        if (wordCount < 4) {
            return view.notEnoughWords(chatId);
        }

        if (wordCount < 10) {
            List<Long> userWordIds = words.getContent().stream()
                                          .map(UserWord::getId)
                                          .toList();

            TrainingSession session = trainingSessionService.create(user, topicId, userWordIds);
            state.finish(user);
            TrainingQuestionData question = trainingService.nextQuestion(user, session.getId());
            return trainingQuestionView.question(chatId, question);
        }

        return learningWordSelectionService.start(user, chatId, topicId);
    }

    public EditMessageText editTopics(TelegramUser user, Long chatId, Integer messageId) {
        state.start(user);

        Page<Topic> result = topicStorage.findTopicsForUser(user, 0);

        if (result.isEmpty()) {
            return view.editEmptyTopics(chatId, messageId);
        }

        state.setPage(user, result.getNumber());

        return view.editTopics(chatId, messageId, result);
    }
}
