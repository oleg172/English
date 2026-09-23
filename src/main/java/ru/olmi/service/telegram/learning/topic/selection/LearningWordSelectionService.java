package ru.olmi.service.telegram.learning.topic.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.domain.TrainingSession;
import ru.olmi.domain.UserWord;
import ru.olmi.dto.TrainingQuestionData;
import ru.olmi.service.impl.training.TrainingService;
import ru.olmi.service.impl.training.TrainingSessionService;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.telegram.learning.topic.selection.state.LearningWordSelectionDialogState;
import ru.olmi.service.telegram.learning.topic.selection.view.LearningWordSelectionView;
import ru.olmi.service.telegram.learning.topic.selection.view.TrainingQuestionTelegramView;

@Component
@RequiredArgsConstructor
public class LearningWordSelectionService {

    private static final int MIN_SELECTED_WORDS = 4;

    private final TopicStorage topicStorage;
    private final LearningWordSelectionDialogState state;
    private final LearningWordSelectionView view;
    private final TrainingSessionService trainingSessionService;
    private final TrainingService trainingService;
    private final TrainingQuestionTelegramView trainingQuestionView;

    public SendMessage start(TelegramUser user, Long chatId, Long topicId) {
        Topic topic = topicStorage.findForUser(user, topicId)
                                  .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));

        Page<UserWord> words = topicStorage.findWordsByTopic(user, topicId, 0);

        if (words.getTotalElements() < 10) {
            throw new IllegalStateException("Word selection is only available for topics with 10 or more words");
        }

        state.start(user, topicId);
        return wordsPage(user, chatId, topic);
    }

    public SendMessage next(TelegramUser user, Long chatId) {
        return page(user, chatId, state.getPage(user) + 1);
    }

    public SendMessage previous(TelegramUser user, Long chatId) {
        return page(user, chatId, state.getPage(user) - 1);
    }

    public SendMessage current(TelegramUser user, Long chatId) {
        return page(user, chatId, state.getPage(user));
    }

    public SendMessage toggleWord(TelegramUser user, Long chatId, Long userWordId) {
        state.toggleWord(user, userWordId);
        return current(user, chatId);
    }

    private SendMessage page(TelegramUser user, Long chatId, int page) {
        if (page < 0) {
            page = 0;
        }

        Long topicId = state.getTopicId(user);

        Topic topic = topicStorage.findForUser(user, topicId)
                                  .orElseThrow(() ->
                                          new IllegalArgumentException("Topic not found: " + topicId));

        Page<UserWord> words = topicStorage.findWordsByTopic(user, topicId, page);

        if (words.isEmpty() && page > 0) {
            return page(user, chatId, page - 1);
        }

        if (words.isEmpty()) {
            throw new IllegalStateException("Topic has no words: " + topicId);
        }

        state.setPage(user, words.getNumber());

        return view.words(chatId, topic, words, state.getSelectedWordIds(user));
    }

    private SendMessage wordsPage(TelegramUser user, Long chatId, Topic topic) {
        Page<UserWord> words = topicStorage.findWordsByTopic(user, topic.getId(), 0);

        state.setPage(user, words.getNumber());

        return view.words(chatId, topic, words, state.getSelectedWordIds(user));
    }

    public SendMessage confirm(TelegramUser user, Long chatId) {
        Set<Long> selectedWordIds = state.getSelectedWordIds(user);

        if (selectedWordIds.size() < MIN_SELECTED_WORDS) {
            return view.notEnoughSelectedWords(chatId);
        }

        Long topicId = state.getTopicId(user);

        TrainingSession session = trainingSessionService.create(user, topicId, List.copyOf(selectedWordIds));
        state.finish(user);
        TrainingQuestionData question = trainingService.nextQuestion(user, session.getId());
        return trainingQuestionView.question(chatId, question);
    }
}
