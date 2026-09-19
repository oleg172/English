package ru.olmi.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Translation;
import ru.olmi.domain.UserWord;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.domain.Word;
import ru.olmi.domain.WordExample;
import ru.olmi.dto.TranslationResult;
import ru.olmi.dto.TranslationSearchResult;
import ru.olmi.repository.TranslationRepository;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.repository.WordRepository;
import ru.olmi.service.storage.WordStorage;

@Component
@RequiredArgsConstructor
public class TranslationFinder {

    private final WordRepository wordRepository;
    private final UserWordRepository userWordRepository;
    private final TranslationRepository translationRepository;
    private final GoogleService googleService;
    private final WordStorage wordStorage;

    @Transactional
    public TranslationSearchResult search(TelegramUser user, String input) {
        if (!containsCyrillic(input)) {
            TranslationResult result = searchByWord(user, input);

            return new TranslationSearchResult().setResults(result == null ? List.of() : List.of(result));
        }

        return searchByTranslation(user, input);
    }

    private TranslationSearchResult searchByTranslation(TelegramUser user, String translation) {
        String fromLanguage = user.getPreferredFromLanguage();
        String toLanguage = user.getPreferredToLanguage();

        List<Word> words = new ArrayList<>(translationRepository.findWordsByTranslation(translation, fromLanguage, toLanguage));

        userWordRepository.findByUserTranslation(user.getId(), translation, fromLanguage, toLanguage).stream()
                          .map(UserWord::getWord)
                          .forEach(word -> {
                              if (words.stream().noneMatch(existing -> existing.getId().equals(word.getId()))) {
                                  words.add(word);
                              }
                          });

        if (words.isEmpty()) {
            TranslationResult engTranslation = googleService.getTranslation(translation, toLanguage, fromLanguage);//ищем английское слово

            Word result = findExternallyAndSave(engTranslation.getCommonTranslations().get(0), fromLanguage, toLanguage);
            return new TranslationSearchResult()
                    .setResults(result == null ? List.of() : List.of(toTranslationResult(user, result)));
        }

        List<TranslationResult> results = words.stream()
                                               .map(w -> toTranslationResult(user, w))
                                               .toList();

        return new TranslationSearchResult()
                .setResults(results);
    }

    @Transactional
    public TranslationResult searchByWord(TelegramUser user, String word) {
        Word existingWord = findOrCreateWord(word, user.getPreferredFromLanguage(), user.getPreferredToLanguage());

        return existingWord == null ? null : toTranslationResult(user, existingWord);
    }

    @Transactional
    public Word findOrCreateWord(String word, String fromLanguage, String toLanguage) {
        return wordRepository.findByWordAndFromLanguageAndToLanguage(word, fromLanguage, toLanguage)
                             .orElseGet(() -> findExternallyAndSave(word, fromLanguage, toLanguage));
    }

    private Word findExternallyAndSave(String word, String fromLanguage, String toLanguage) {
        TranslationResult result = googleService.getTranslation(word, fromLanguage, toLanguage);

        if (result == null) {
            return null;
        }

        return wordStorage.save(result, fromLanguage, toLanguage);
    }

    private boolean containsCyrillic(String text) {
        return text.codePoints()
                   .anyMatch(codePoint -> Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.CYRILLIC);
    }

    private TranslationResult toTranslationResult(TelegramUser user, Word word) {
        TranslationResult result = new TranslationResult()
                .setWordId(word.getId())
                .setWord(word.getWord())
                .setTranscription(word.getTranscription())
                .setCommonTranslations(word.getTranslations().stream().map(Translation::getTranslation).collect(Collectors.toList()))
                .setUsageExamples(word.getExamples().stream().map(WordExample::getExample).collect(Collectors.toList()));

        userWordRepository.findByUserIdAndWordId(user.getId(), word.getId())
                          .ifPresent(userWord -> result.setUserTranslations(userWord.getTranslations().stream().map(UserWordTranslation::getTranslation)
                                                                                    .collect(Collectors.toList()))
                                                       .setTopics(userWord.getTopics().stream().map(t -> t.getTopic().getName()).collect(Collectors.toList())));

        return result;
    }
}
