package ru.olmi.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.dictionary.topic.search.topic.UserDictionaryTopicSearchState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDictionaryTopicSearchStateTest {

    private static final Long USER_ID = 1L;

    private UserDictionaryTopicSearchState state;
    private TelegramUser user;

    @BeforeEach
    void setUp() {
        state = new UserDictionaryTopicSearchState();

        user = mock(TelegramUser.class);
        when(user.getId()).thenReturn(USER_ID);
    }

    @Test
    void shouldReturnDefaultStateForUnknownUser() {
        assertEquals("", state.getQuery(user));
        assertEquals(0, state.getPage(user));
        assertFalse(state.isWaitingForQuery(user));
    }

    @Test
    void shouldInitializeStateOnStart() {
        state.start(user);

        assertEquals("", state.getQuery(user));
        assertEquals(0, state.getPage(user));
        assertTrue(state.isWaitingForQuery(user));
    }

    @Test
    void shouldSetQueryAndResetPage() {
        state.start(user);
        state.setPage(user, 3);
        state.setQuery(user, "java");

        assertEquals("java", state.getQuery(user));
        assertEquals(0, state.getPage(user));
        assertFalse(state.isWaitingForQuery(user));
    }

    @Test
    void shouldChangePageAndPreserveQuery() {
        state.start(user);
        state.setQuery(user, "java");
        state.setPage(user, 4);

        assertEquals("java", state.getQuery(user));
        assertEquals(4, state.getPage(user));
        assertFalse(state.isWaitingForQuery(user));
    }

    @Test
    void shouldBeWaitingForQueryOnlyWhenStartedWithoutQuery() {
        state.start(user);
        assertTrue(state.isWaitingForQuery(user));

        state.setQuery(user, "java");
        assertFalse(state.isWaitingForQuery(user));
    }

    @Test
    void shouldNotBeWaitingForQueryAfterFinish() {
        state.start(user);
        state.finish(user);

        assertFalse(state.isWaitingForQuery(user));
        assertEquals("", state.getQuery(user));
        assertEquals(0, state.getPage(user));
    }
}
