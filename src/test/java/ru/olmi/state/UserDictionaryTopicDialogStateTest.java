package ru.olmi.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.olmi.domain.TelegramUser;
import ru.olmi.service.telegram.dictionary.topic.state.UserDictionaryTopicDialogState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDictionaryTopicDialogStateTest {

    private static final Long USER_ID = 1L;

    private UserDictionaryTopicDialogState state;
    private TelegramUser user;

    @BeforeEach
    void setUp() {
        state = new UserDictionaryTopicDialogState();

        user = mock(TelegramUser.class);
        when(user.getId()).thenReturn(USER_ID);
    }

    @Test
    void shouldReturnDefaultStateForUnknownUser() {
        assertNull(state.getTopicId(user));
        assertEquals(0, state.getTopicPage(user));
        assertEquals(0, state.getWordPage(user));
    }

    @Test
    void shouldInitializeDefaultStateOnStart() {
        state.start(user);

        assertNull(state.getTopicId(user));
        assertEquals(0, state.getTopicPage(user));
        assertEquals(0, state.getWordPage(user));
    }

    @Test
    void shouldChangeTopicPageAndPreserveOtherState() {
        state.start(user);
        state.setTopic(user, 100L);
        state.setWordPage(user, 3);

        state.setTopicPage(user, 5);

        assertEquals(100L, state.getTopicId(user));
        assertEquals(5, state.getTopicPage(user));
        assertEquals(3, state.getWordPage(user));
    }

    @Test
    void shouldSetTopicAndResetWordPage() {
        state.start(user);
        state.setTopicPage(user, 4);
        state.setWordPage(user, 7);

        state.setTopic(user, 100L);

        assertEquals(100L, state.getTopicId(user));
        assertEquals(4, state.getTopicPage(user));
        assertEquals(0, state.getWordPage(user));
    }

    @Test
    void shouldChangeWordPageAndPreserveOtherState() {
        state.start(user);
        state.setTopicPage(user, 2);
        state.setTopic(user, 100L);

        state.setWordPage(user, 6);

        assertEquals(100L, state.getTopicId(user));
        assertEquals(2, state.getTopicPage(user));
        assertEquals(6, state.getWordPage(user));
    }

    @Test
    void shouldRemoveStateOnFinish() {
        state.start(user);
        state.setTopicPage(user, 5);
        state.setTopic(user, 100L);
        state.setWordPage(user, 3);

        state.finish(user);

        assertNull(state.getTopicId(user));
        assertEquals(0, state.getTopicPage(user));
        assertEquals(0, state.getWordPage(user));
    }
}
