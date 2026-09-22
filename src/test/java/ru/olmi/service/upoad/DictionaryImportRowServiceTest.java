package ru.olmi.service.upoad;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.Word;
import ru.olmi.dto.DictionaryCsvRow;
import ru.olmi.dto.DictionaryImportRowResult;
import ru.olmi.dto.enums.DictionaryImportRowStatus;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.service.impl.TranslationFinder;
import ru.olmi.service.storage.TopicStorage;
import ru.olmi.service.storage.UserWordStorage;
import ru.olmi.service.telegram.upload.DictionaryImportRowService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryImportRowServiceTest {

    @Mock
    private TranslationFinder translationFinder;

    @Mock
    private UserWordRepository userWordRepository;

    @Mock
    private UserWordStorage userWordStorage;

    @Mock
    private TopicStorage topicStorage;

    @Mock
    private TelegramUser user;

    @InjectMocks
    private DictionaryImportRowService service;

    @Test
    void shouldReturnAddedForNewUserWord() {
        Word word = new Word()
                .setId(1L)
                .setWord("reliable");

        UserWord userWord = new UserWord()
                .setId(10L)
                .setUser(user)
                .setWord(word);

        DictionaryCsvRow row = new DictionaryCsvRow(
                "reliable",
                List.of("надёжный"),
                List.of("Work")
        );

        when(user.getId()).thenReturn(100L);
        when(user.getPreferredFromLanguage()).thenReturn("en");
        when(user.getPreferredToLanguage()).thenReturn("ru");
        when(translationFinder.findOrCreateWord("reliable", "en", "ru")).thenReturn(word);
        when(userWordRepository.findByUserIdAndWordId(100L, 1L)).thenReturn(Optional.empty());
        when(userWordStorage.findOrCreate(user, word)).thenReturn(userWord);
        when(userWordStorage.addTranslation(user, 10L, "надёжный")).thenReturn(true);
        when(topicStorage.addTopic(user, 10L, "Work")).thenReturn(true);

        DictionaryImportRowResult result = service.importRow(user, row);

        assertThat(result.status()).isEqualTo(DictionaryImportRowStatus.ADDED);
    }

    @Test
    void shouldReturnUnchangedWhenExistingUserWordWasNotChanged() {
        Word word = new Word()
                .setId(1L)
                .setWord("reliable");

        UserWord userWord = new UserWord()
                .setId(10L)
                .setUser(user)
                .setWord(word);

        DictionaryCsvRow row = new DictionaryCsvRow(
                "reliable",
                List.of("надёжный"),
                List.of("Work")
        );

        when(user.getId()).thenReturn(100L);
        when(user.getPreferredFromLanguage()).thenReturn("en");
        when(user.getPreferredToLanguage()).thenReturn("ru");
        when(translationFinder.findOrCreateWord("reliable", "en", "ru")).thenReturn(word);
        when(userWordRepository.findByUserIdAndWordId(100L, 1L)).thenReturn(Optional.of(userWord));
        when(userWordStorage.findOrCreate(user, word)).thenReturn(userWord);
        when(userWordStorage.addTranslation(user, 10L, "надёжный")).thenReturn(false);
        when(topicStorage.addTopic(user, 10L, "Work")).thenReturn(false);

        DictionaryImportRowResult result = service.importRow(user, row);

        assertThat(result.status()).isEqualTo(DictionaryImportRowStatus.UNCHANGED);
    }

    @Test
    void shouldPropagateException() {
        DictionaryCsvRow row = new DictionaryCsvRow(
                "reliable",
                List.of(),
                List.of()
        );

        when(user.getPreferredFromLanguage()).thenReturn("en");
        when(user.getPreferredToLanguage()).thenReturn("ru");
        when(translationFinder.findOrCreateWord("reliable", "en", "ru")).thenThrow(new RuntimeException("Google translation failed"));
        assertThatThrownBy(() -> service.importRow(user, row)).isInstanceOf(RuntimeException.class).hasMessage("Google translation failed");
    }

    @Test
    void shouldReturnAddedWhenUserWordDoesNotExist() {
        TelegramUser user = new TelegramUser()
                .setId(1L)
                .setPreferredFromLanguage("en")
                .setPreferredToLanguage("ru");

        Word word = new Word()
                .setId(10L)
                .setWord("reliable");

        DictionaryCsvRow row = new DictionaryCsvRow(
                "reliable",
                List.of("надёжный"),
                List.of("Work")
        );

        when(translationFinder.findOrCreateWord("reliable", "en", "ru")).thenReturn(word);
        when(userWordRepository.findByUserIdAndWordId(1L, 10L)).thenReturn(Optional.empty());

        UserWord userWord = new UserWord()
                .setId(100L)
                .setUser(user)
                .setWord(word);

        when(userWordStorage.findOrCreate(user, word)).thenReturn(userWord);
        when(userWordStorage.addTranslation(user, 100L, "надёжный")).thenReturn(true);
        when(topicStorage.addTopic(user, 100L, "Work")).thenReturn(true);

        DictionaryImportRowResult result = service.importRow(user, row);

        assertThat(result.status()).isEqualTo(DictionaryImportRowStatus.ADDED);

        verify(userWordStorage).findOrCreate(user, word);
    }

    @Test
    void shouldReturnUpdatedWhenExistingUserWordWasChanged() {
        TelegramUser user = new TelegramUser()
                .setId(1L)
                .setPreferredFromLanguage("en")
                .setPreferredToLanguage("ru");

        Word word = new Word()
                .setId(10L)
                .setWord("reliable");

        DictionaryCsvRow row = new DictionaryCsvRow(
                "reliable",
                List.of("надёжный"),
                List.of("Work")
        );

        when(translationFinder.findOrCreateWord("reliable", "en", "ru")).thenReturn(word);
        when(userWordRepository.findByUserIdAndWordId(1L, 10L)).thenReturn(Optional.of(new UserWord().setId(100L).setUser(user).setWord(word)));

        UserWord userWord = new UserWord()
                .setId(100L)
                .setUser(user)
                .setWord(word);

        when(userWordStorage.findOrCreate(user, word)).thenReturn(userWord);
        when(userWordStorage.addTranslation(user, 100L, "надёжный")).thenReturn(true);
        when(topicStorage.addTopic(user, 100L, "Work")).thenReturn(false);

        DictionaryImportRowResult result = service.importRow(user, row);

        assertThat(result.status()).isEqualTo(DictionaryImportRowStatus.UPDATED);
    }

    @Test
    void shouldReturnUnchangedWhenNothingWasAdded() {
        TelegramUser user = new TelegramUser()
                .setId(1L)
                .setPreferredFromLanguage("en")
                .setPreferredToLanguage("ru");

        Word word = new Word()
                .setId(10L)
                .setWord("reliable");

        DictionaryCsvRow row = new DictionaryCsvRow(
                "reliable",
                List.of("надёжный"),
                List.of("Work")
        );

        when(translationFinder.findOrCreateWord("reliable", "en", "ru")).thenReturn(word);
        when(userWordRepository.findByUserIdAndWordId(1L, 10L)).thenReturn(Optional.of(new UserWord().setId(100L).setUser(user).setWord(word)));

        UserWord userWord = new UserWord()
                .setId(100L)
                .setUser(user)
                .setWord(word);

        when(userWordStorage.findOrCreate(user, word)).thenReturn(userWord);
        when(userWordStorage.addTranslation(user, 100L, "надёжный")).thenReturn(false);
        when(topicStorage.addTopic(user, 100L, "Work")).thenReturn(false);

        DictionaryImportRowResult result = service.importRow(user, row);

        assertThat(result.status()).isEqualTo(DictionaryImportRowStatus.UNCHANGED);
    }
}