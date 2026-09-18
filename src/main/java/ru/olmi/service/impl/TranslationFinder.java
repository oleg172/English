package ru.olmi.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.olmi.domain.TelegramUser;
import ru.olmi.domain.Translation;
import ru.olmi.domain.UserWordTranslation;
import ru.olmi.domain.Word;
import ru.olmi.domain.WordExample;
import ru.olmi.dto.TranslationResult;
import ru.olmi.repository.UserWordRepository;
import ru.olmi.repository.WordRepository;
import ru.olmi.service.storage.WordStorage;

@Component
@RequiredArgsConstructor
public class TranslationFinder {

    private final WordRepository wordRepository;
    private final UserWordRepository userWordRepository;
    private final GoogleService googleService;
    private final WordStorage wordStorage;

    @Transactional
    public TranslationResult find(TelegramUser user, String word) {
        Optional<Word> existingWordOpt = Optional.empty();
        if (!containsCyrillic(word)) {
            existingWordOpt = wordRepository.findByWordAndFromLanguageAndToLanguage(
                    word,
                    user.getPreferredFromLanguage(),
                    user.getPreferredToLanguage()
            );
        } else {

        }

        Word existingWord = existingWordOpt.orElseGet(() -> findExternallyAndSave(word, user.getPreferredFromLanguage(), user.getPreferredToLanguage()));

        TranslationResult translationResult = new TranslationResult()
                .setWordId(existingWord.getId())
                .setWord(word)
                .setTranscription(existingWord.getTranscription())
                .setCommonTranslations(existingWord.getTranslations().stream().map(Translation::getTranslation).collect(Collectors.toList()))
                .setUsageExamples(existingWord.getExamples().stream().map(WordExample::getExample).collect(Collectors.toList()));

        var userWord = userWordRepository.findByUserIdAndWordId(user.getId(), existingWord.getId());

        if (userWord.isPresent()) {
            translationResult.setUserTranslations(
                                     userWord.get().getTranslations().stream().map(UserWordTranslation::getTranslation).collect(Collectors.toList()))
                             .setTopics(userWord.get().getTopics().stream().map(t -> t.getTopic().getName()).collect(Collectors.toList()));
        }
        return translationResult;
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
}
