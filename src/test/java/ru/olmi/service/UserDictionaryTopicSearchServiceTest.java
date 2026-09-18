package ru.olmi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Topic;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchService;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchState;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchView;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class UserDictionaryTopicSearchServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long CHAT_ID = 10L;

    private UserDictionaryTopicSearchService service;
    private TopicStorage topicStorage;
    private UserDictionaryTopicSearchState state;
    private UserDictionaryTopicSearchView view;
    private TelegramUser user;

    @BeforeEach
    void setUp() {
        topicStorage = mock(TopicStorage.class);
        state = mock(UserDictionaryTopicSearchState.class);
        view = mock(UserDictionaryTopicSearchView.class);

        service = new UserDictionaryTopicSearchService(topicStorage, state, view);

        user = mock(TelegramUser.class);
        when(user.getId()).thenReturn(USER_ID);
    }

    @Test
    void start_shouldStartStateAndShowQueryInput() {
        SendMessage message = mock(SendMessage.class);

        when(view.enterQuery(CHAT_ID)).thenReturn(message);

        SendMessage result = service.start(user, CHAT_ID);

        assertSame(message, result);

        verify(state).start(user);
        verify(view).enterQuery(CHAT_ID);
    }

    @Test
    void search_shouldTrimQuerySetStateAndShowFirstPage() {
        String query = "java";
        Topic topic = mock(Topic.class);
        Page<Topic> page = topicsPage(0, topic);

        SendMessage message = mock(SendMessage.class);

        when(topicStorage.searchTopicsForUser(user, query, 0)).thenReturn(page);
        when(state.getQuery(user)).thenReturn(query);
        when(view.results(CHAT_ID, query, page)).thenReturn(message);

        SendMessage result = service.search(user, CHAT_ID, "  java  ");

        assertSame(message, result);

        verify(state).setQuery(user, query);
        verify(topicStorage).searchTopicsForUser(user, query, 0);
        verify(state).setPage(user, 0);
        verify(view).results(CHAT_ID, query, page);
    }

    @Test
    void search_shouldAskForQueryWhenQueryIsEmpty() {
        SendMessage message = mock(SendMessage.class);

        when(view.enterQuery(CHAT_ID)).thenReturn(message);

        SendMessage result = service.search(user, CHAT_ID, "   ");

        assertSame(message, result);

        verify(view).enterQuery(CHAT_ID);
        verify(state, never()).setQuery(any(), anyString());
        verifyNoInteractions(topicStorage);
    }

    @Test
    void next_shouldRequestNextPage() {
        String query = "java";
        Page<Topic> page = topicsPage(2, mock(Topic.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getPage(user)).thenReturn(1);
        when(state.getQuery(user)).thenReturn(query);
        when(topicStorage.searchTopicsForUser(user, query, 2)).thenReturn(page);
        when(view.results(CHAT_ID, query, page)).thenReturn(message);

        SendMessage result = service.next(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).searchTopicsForUser(user, query, 2);
        verify(state).setPage(user, 2);
        verify(view).results(CHAT_ID, query, page);
    }

    @Test
    void previous_shouldRequestPreviousPage() {
        String query = "java";
        Page<Topic> page = topicsPage(2, mock(Topic.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getPage(user)).thenReturn(3);
        when(state.getQuery(user)).thenReturn(query);
        when(topicStorage.searchTopicsForUser(user, query, 2)).thenReturn(page);
        when(view.results(CHAT_ID, query, page)).thenReturn(message);

        SendMessage result = service.previous(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).searchTopicsForUser(user, query, 2);
        verify(state).setPage(user, 2);
        verify(view).results(CHAT_ID, query, page);
    }

    @Test
    void previous_shouldNotRequestNegativePage() {
        String query = "java";
        Page<Topic> page = topicsPage(0, mock(Topic.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getPage(user)).thenReturn(0);
        when(state.getQuery(user)).thenReturn(query);
        when(topicStorage.searchTopicsForUser(user, query, 0)).thenReturn(page);
        when(view.results(CHAT_ID, query, page)).thenReturn(message);

        SendMessage result = service.previous(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).searchTopicsForUser(user, query, 0);
        verify(topicStorage, never())
                .searchTopicsForUser(user, query, -1);
    }

    @Test
    void current_shouldShowCurrentPage() {
        String query = "java";
        Page<Topic> page = topicsPage(2, mock(Topic.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getPage(user)).thenReturn(2);
        when(state.getQuery(user)).thenReturn(query);
        when(topicStorage.searchTopicsForUser(user, query, 2)).thenReturn(page);
        when(view.results(CHAT_ID, query, page)).thenReturn(message);

        SendMessage result = service.current(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).searchTopicsForUser(user, query, 2);
        verify(state).setPage(user, 2);
        verify(view).results(CHAT_ID, query, page);
    }

    @Test
    void current_shouldReturnPreviousPageWhenRequestedPageIsEmpty() {
        String query = "java";

        Page<Topic> emptyPage = topicsPage(3);
        Page<Topic> previousPage = topicsPage(2, mock(Topic.class));

        SendMessage message = mock(SendMessage.class);

        when(state.getPage(user)).thenReturn(3);
        when(state.getQuery(user)).thenReturn(query);
        when(topicStorage.searchTopicsForUser(user, query, 3)).thenReturn(emptyPage);
        when(topicStorage.searchTopicsForUser(user, query, 2)).thenReturn(previousPage);
        when(view.results(CHAT_ID, query, previousPage)).thenReturn(message);

        SendMessage result = service.current(user, CHAT_ID);

        assertSame(message, result);

        verify(topicStorage).searchTopicsForUser(user, query, 3);
        verify(topicStorage).searchTopicsForUser(user, query, 2);
        verify(state).setPage(user, 2);
        verify(view).results(CHAT_ID, query, previousPage);
    }

    @Test
    void current_shouldShowEmptyWhenThereAreNoResults() {
        String query = "java";
        Page<Topic> emptyPage = topicsPage(0);

        SendMessage message = mock(SendMessage.class);

        when(state.getPage(user)).thenReturn(0);
        when(state.getQuery(user)).thenReturn(query);
        when(topicStorage.searchTopicsForUser(user, query, 0)).thenReturn(emptyPage);
        when(view.empty(CHAT_ID, query)).thenReturn(message);

        SendMessage result = service.current(user, CHAT_ID);

        assertSame(message, result);

        verify(view).empty(CHAT_ID, query);
        verify(state, never()).setPage(user, 0);
    }

    private Page<Topic> topicsPage(int page, Topic... topics) {
        return new PageImpl<>(
                List.of(topics),
                PageRequest.of(page, 10),
                topics.length
        );
    }
}
