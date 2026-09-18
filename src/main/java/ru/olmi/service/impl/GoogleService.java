package ru.olmi.service.impl;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.olmi.dto.TranslationResult;
import ru.olmi.dto.TranslationSource;
import ru.olmi.integration.google.GoogleTranslationResponse;
import ru.olmi.service.integration.TranslateIntegration;

@Service
@AllArgsConstructor
@Slf4j
public class GoogleService {

    private final TranslateIntegration googleTranslateIntegration;

    @Cacheable(value = "english", key = "#word + ':' + #language + ':' + #targetLanguage")
    public TranslationResult getTranslation(String word, String language, String targetLanguage) {
        log.info("Try to find translation for word [{}] from language [{}] to language [{}] in google", word, language, targetLanguage);

        GoogleTranslationResponse response = (GoogleTranslationResponse) googleTranslateIntegration.translate(word, language, targetLanguage);

        TranslationResult result = null;
        if (response != null) {
            result = new TranslationResult();
            result.setWord(word)
                  .setTranscription(response.getSentences()
                                            .stream()
                                            .filter(s -> s.getSrcTranslit() != null)
                                            .findFirst()
                                            .orElse(new GoogleTranslationResponse.Sentence())
                                            .getSrcTranslit())
                  .setCommonTranslations(response.getAlternativeTranslations().stream()
                                                 .flatMap(a -> a.getAlternative().stream())
                                                 .map(GoogleTranslationResponse.Alternative::getWordPostproc)
                                                 .collect(Collectors.toList()))
                  .setTranslationSource(TranslationSource.EXTERNAL_SOURCE);
            if (response.getExamples() != null && !CollectionUtils.isEmpty(response.getExamples().getExample())) {
                result.setUsageExamples(response.getExamples().getExample().stream()
                                                .map(GoogleTranslationResponse.Example::getText)
                                                .map(t -> t.replace("<b>", "*"))
                                                .map(t -> t.replace("</b>", "*"))
                                                .collect(Collectors.toList())
                );
            }
        }
        return result;
    }
}
