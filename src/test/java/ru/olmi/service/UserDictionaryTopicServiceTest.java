package ru.olmi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.domain.UserWord;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.telegram.dictionary.topic.UserDictionaryTopicService;
import ru.olmi.service.telegram.dictionary.topic.UserDictionaryTopicView;
import ru.olmi.service.telegram.dictionary.topic.state.UserDictionaryTopicDialogState;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDictionaryTopicServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long CHAT_ID = 100L;
    private static final Long TOPIC_ID = 10L;

    private UserDictionaryTopicService service;
    private TopicStorage topicStorage;
    private UserDictionaryTopicDialogState state;
    private UserDictionaryTopicView view;
    private TelegramUser user;

    @BeforeEach
    void setUp() {
        topicStorage = mock(TopicStorage.class);
        state = mock(UserDictionaryTopicDialogState.class);
        view = mock(UserDictionaryTopicView.class);
        user = mock(TelegramUser.class);

        when(user.getId()).thenReturn(USER_ID);

        service = new UserDictionaryTopicService(topicStorage, state, view);
    }

    @Test
    void topics_shouldStartStateAndShowFirstPage() {
        Page<Topic> page = topicsPage(0, topic());

        SendMessage message = mock(SendMessage.class);

        when(topicStorage.findTopicsForUser(user, 0)).thenReturn(page);
        when(view.topics(CHAT_ID, page)).thenReturn(message);

        SendMessage result = service.topics(user, CHAT_ID);

        assertSame(message, result);

        verify(state).start(user);
        verify(topicStorage).findTopicsForUser(user, 0);
        verify(state).setTopicPage(user, 0);
        verify(view).topics(CHAT_ID, page);
    }

    @Test
    void nextTopics_shouldRequestNextPage() {
        Page<Topic> page = topicsPage(2, topic());

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicPage(user)).thenReturn(1);
        when(topicStorage.findTopicsForUser(user, 2)).thenReturn(page);
        when(view.topics(CHAT_ID, page)).thenReturn(message);

        SendMessage result = service.nextTopics(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findTopicsForUser(user, 2);
        verify(state).setTopicPage(user, 2);
        verify(view).topics(CHAT_ID, page);
    }

    @Test
    void previousTopics_shouldNotRequestNegativePage() {
        Page<Topic> page = topicsPage(0, topic());

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicPage(user)).thenReturn(0);
        when(topicStorage.findTopicsForUser(user, 0)).thenReturn(page);
        when(view.topics(CHAT_ID, page)).thenReturn(message);

        SendMessage result = service.previousTopics(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findTopicsForUser(user, 0);
        verify(topicStorage, never()).findTopicsForUser(user, -1);
    }

    @Test
    void currentTopics_shouldShowCurrentPage() {
        Page<Topic> page = topicsPage(3, topic());

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicPage(user)).thenReturn(3);
        when(topicStorage.findTopicsForUser(user, 3)).thenReturn(page);
        when(view.topics(CHAT_ID, page)).thenReturn(message);

        SendMessage result = service.currentTopics(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findTopicsForUser(user, 3);
        verify(view).topics(CHAT_ID, page);
    }

    @Test
    void currentTopics_shouldReturnPreviousPageWhenRequestedPageIsEmpty() {
        Page<Topic> emptyPage = topicsPage(3);
        Page<Topic> previousPage = topicsPage(2, topic());

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicPage(user)).thenReturn(3);
        when(topicStorage.findTopicsForUser(user, 3)).thenReturn(emptyPage);
        when(topicStorage.findTopicsForUser(user, 2)).thenReturn(previousPage);
        when(view.topics(CHAT_ID, previousPage)).thenReturn(message);

        SendMessage result = service.currentTopics(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findTopicsForUser(user, 3);
        verify(topicStorage).findTopicsForUser(user, 2);
        verify(state).setTopicPage(user, 2);
        verify(view).topics(CHAT_ID, previousPage);
    }

    @Test
    void currentTopics_shouldReturnEmptyTopicsWhenThereAreNoTopics() {
        Page<Topic> emptyPage = topicsPage(0);

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicPage(user)).thenReturn(0);
        when(topicStorage.findTopicsForUser(user, 0)).thenReturn(emptyPage);
        when(view.emptyTopics(CHAT_ID)).thenReturn(message);

        SendMessage result = service.currentTopics(user, CHAT_ID);

        assertSame(message, result);

        verify(view).emptyTopics(CHAT_ID);
        verify(state, never()).setTopicPage(any(), any(Integer.class));
    }

    @Test
    void selectTopic_shouldReturnEmptyTopicsWhenTopicDoesNotBelongToUser() {
        SendMessage message = mock(SendMessage.class);

        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.empty());
        when(view.emptyTopics(CHAT_ID)).thenReturn(message);

        SendMessage result = service.selectTopic(user, CHAT_ID, TOPIC_ID);

        assertSame(message, result);

        verify(state, never()).setTopic(user, TOPIC_ID);
        verify(view).emptyTopics(CHAT_ID);
    }

    @Test
    void selectTopic_shouldSetTopicAndShowFirstWordsPage() {
        Topic topic = topic();
        Page<UserWord> page = wordsPage(0, mock(UserWord.class));
        SendMessage message = mock(SendMessage.class);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.of(topic));
        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 0)).thenReturn(page);
        when(view.words(CHAT_ID, topic, page)).thenReturn(message);

        SendMessage result = service.selectTopic(user, CHAT_ID, TOPIC_ID);

        assertSame(message, result);

        verify(state).setTopic(user, TOPIC_ID);
        verify(topicStorage).findWordsByTopic(user, TOPIC_ID, 0);
        verify(state).setWordPage(user, 0);
        verify(view).words(CHAT_ID, topic, page);
    }

    @Test
    void nextWords_shouldRequestNextPage() {
        Topic topic = topic();
        Page<UserWord> page = wordsPage(2, mock(UserWord.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(state.getWordPage(user)).thenReturn(1);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.of(topic));
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 2)).thenReturn(page);
        when(view.words(CHAT_ID, topic, page)).thenReturn(message);

        SendMessage result = service.nextWords(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findWordsByTopic(user, TOPIC_ID, 2);
        verify(state).setWordPage(user, 2);
        verify(view).words(CHAT_ID, topic, page);
    }

    @Test
    void previousWords_shouldNotRequestNegativePage() {
        Topic topic = topic();
        Page<UserWord> page = wordsPage(0, mock(UserWord.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(state.getWordPage(user)).thenReturn(0);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.of(topic));
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 0)).thenReturn(page);
        when(view.words(CHAT_ID, topic, page)).thenReturn(message);

        SendMessage result = service.previousWords(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findWordsByTopic(user, TOPIC_ID, 0);
        verify(topicStorage, never()).findWordsByTopic(user, TOPIC_ID, -1);
    }

    @Test
    void currentWords_shouldReturnEmptyTopicsWhenTopicIsNotSelected() {
        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(null);
        when(view.emptyTopics(CHAT_ID)).thenReturn(message);

        SendMessage result = service.currentWords(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage, never()).findWordsByTopic(any(), any(), any(Integer.class));
        verify(view).emptyTopics(CHAT_ID);
    }

    @Test
    void currentWords_shouldReturnEmptyTopicsWhenSelectedTopicDoesNotExist() {
        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.empty());
        when(view.emptyTopics(CHAT_ID)).thenReturn(message);

        SendMessage result = service.currentWords(user, CHAT_ID);

        assertSame(message, result);

        verify(view).emptyTopics(CHAT_ID);
        verify(topicStorage, never()).findWordsByTopic(any(), any(), any(Integer.class));
    }

    @Test
    void currentWords_shouldReturnPreviousPageWhenRequestedPageIsEmpty() {
        Topic topic = topic();

        Page<UserWord> emptyPage = wordsPage(3);
        Page<UserWord> previousPage = wordsPage(2, mock(UserWord.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(state.getWordPage(user)).thenReturn(3);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.of(topic));
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 3)).thenReturn(emptyPage);
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 2)).thenReturn(previousPage);
        when(view.words(CHAT_ID, topic, previousPage)).thenReturn(message);

        SendMessage result = service.currentWords(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).findWordsByTopic(user, TOPIC_ID, 3);
        verify(topicStorage).findWordsByTopic(user, TOPIC_ID, 2);
        verify(state).setWordPage(user, 2);
        verify(view).words(CHAT_ID, topic, previousPage);
    }

    @Test
    void currentWords_shouldReturnEmptyWordsWhenTopicHasNoWords() {
        Topic topic = topic();
        Page<UserWord> emptyPage = wordsPage(0);

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.of(topic));
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 0)).thenReturn(emptyPage);
        when(view.emptyWords(CHAT_ID, topic)).thenReturn(message);

        SendMessage result = service.currentWords(user, CHAT_ID);

        assertSame(message, result);

        verify(view).emptyWords(CHAT_ID, topic);
        verify(state, never()).setWordPage(any(), any(Integer.class));
    }

    @Test
    void currentWords_shouldShowWordsPage() {
        Topic topic = topic();
        Page<UserWord> page = wordsPage(2, mock(UserWord.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getTopicId(user)).thenReturn(TOPIC_ID);
        when(state.getWordPage(user)).thenReturn(2);
        when(topicStorage.findForUser(user, TOPIC_ID)).thenReturn(Optional.of(topic));
        when(topicStorage.findWordsByTopic(user, TOPIC_ID, 2)).thenReturn(page);
        when(view.words(CHAT_ID, topic, page)).thenReturn(message);

        SendMessage result = service.currentWords(user, CHAT_ID);

        assertSame(message, result);

        verify(state).setWordPage(user, 2);
        verify(view).words(CHAT_ID, topic, page);
    }

    private Page<Topic> topicsPage(int page, Topic... topics) {
        Pageable pageable = PageRequest.of(page, 10);
        return new PageImpl<>(List.of(topics), pageable, topics.length);
    }

    private Page<UserWord> wordsPage(int page, UserWord... words) {
        Pageable pageable = PageRequest.of(page, 10);
        return new PageImpl<>(List.of(words), pageable, words.length);
    }

    private Topic topic() {
        return mock(Topic.class);
    }
}
