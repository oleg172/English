package ru.olmi.service.storage;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Translation;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.domain.Word;
import ru.olmi.domain.WordExample;
import ru.olmi.dto.TranslationResult;
import ru.olmi.repository.UserWordRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class UserWordStorage {

    private static final int PAGE_SIZE = 10;

    private final UserWordRepository userWordRepository;

    public UserWord findOrCreate(TelegramUser user, Word word) {
        return userWordRepository.findByUserIdAndWordId(user.getId(), word.getId())
                                 .orElseGet(() -> userWordRepository.save(new UserWord().setUser(user).setWord(word)));
    }

    @Transactional(readOnly = true)
    public Page<UserWord> search(TelegramUser user, String query, int page) {
        return userWordRepository.search(user.getId(), query, PageRequest.of(page, PAGE_SIZE));
    }

    @Transactional(readOnly = true)
    public Optional<TranslationResult> findForView(TelegramUser user, Long userWordId) {
        UserWord userWord = userWordRepository.findByIdAndUserIdWithWord(userWordId, user.getId())
                                              .orElse(null);

        if (userWord == null) {
            return Optional.empty();
        }

        return Optional.of(new TranslationResult()
                .setWord(userWord.getWord().getWord())
                .setTranscription(userWord.getWord().getTranscription())
                .setUserTranslations(userWord.getTranslations()
                                             .stream()
                                             .map(UserWordTranslation::getTranslation)
                                             .toList()
                )
                .setCommonTranslations(userWord.getWord().getTranslations()
                                               .stream()
                                               .map(Translation::getTranslation)
                                               .toList()
                )
                .setUsageExamples(userWord.getWord().getExamples()
                                          .stream()
                                          .map(WordExample::getExample)
                                          .toList()
                )
                .setTopics(userWord.getTopics()
                                   .stream()
                                   .map(value -> value.getTopic().getName())
                                   .toList()
                )
        );
    }

    @Transactional
    public boolean addTranslation(TelegramUser user, Long userWordId, String translation) {
        UserWord userWord = userWordRepository.findByIdAndUserId(userWordId, user.getId())
                                              .orElseThrow(() -> new IllegalArgumentException("User word not found: " + userWordId));

        boolean alreadyExists = userWord.getTranslations()
                                        .stream()
                                        .anyMatch(value -> value.getTranslation().equalsIgnoreCase(translation));

        if (alreadyExists) {
            return false;
        }

        userWord.getTranslations().add(new UserWordTranslation().setUserWord(userWord).setTranslation(translation));
        return true;
    }

    @Transactional(readOnly = true)
    public List<UserWordTranslation> findTranslationsForEdit(TelegramUser user, Long userWordId) {
        return userWordRepository.findByIdAndUserIdWithTranslations(userWordId, user.getId())
                                 .map(UserWord::getTranslations)
                                 .orElse(List.of());
    }

    @Transactional
    public boolean deleteTranslation(TelegramUser user, Long translationId) {
        UserWordTranslation translation = userWordRepository.findTranslationByIdAndUserId(translationId, user.getId())
                                                            .orElse(null);

        if (translation == null) {
            return false;
        }

        UserWord userWord = translation.getUserWord();

        userWord.getTranslations().remove(translation);

        return true;
    }

    @Transactional
    public boolean delete(TelegramUser user, Long userWordId) {
        UserWord userWord = userWordRepository.findByIdAndUserId(userWordId, user.getId()).orElse(null);

        if (userWord == null) {
            return false;
        }

        userWordRepository.delete(userWord);

        return true;
    }

    @Transactional(readOnly = true)
    public Page<UserWord> findPage(TelegramUser user, int page) {
        return userWordRepository.findAllByUserIdOrderByIdDesc(user.getId(), PageRequest.of(page, PAGE_SIZE));
    }
}
