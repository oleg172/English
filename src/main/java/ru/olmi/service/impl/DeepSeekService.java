package ru.olmi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.olmi.dto.TranslationResult;
import ru.olmi.integration.deepseek.DeepSeekRequest;
import ru.olmi.integration.deepseek.DeepSeekResponse;
import ru.olmi.service.RateLimiterService;
import ru.olmi.service.integration.DeepSeekIntegration;

import static ru.olmi.utils.DeepSeekParserUtil.extractTranslation;

@Service
@AllArgsConstructor
@Slf4j
public class DeepSeekService {

    private final RateLimiterService deepSeekRateLimiterService;
    private final DeepSeekIntegration integration;
    private final ObjectMapper objectMapper;

    @Cacheable(value = "english", key = "#word + ':' + #language + ':' + #targetLanguage")
    public TranslationResult getTranslation(String word, String language, String targetLanguage) {
        deepSeekRateLimiterService.checkRateLimit();
        log.info("Try to find translation for word [{}] from language [{}] to language [{}] in deepSeek", word, language, targetLanguage);

        String prompt = buildTranslationPrompt(word, language, targetLanguage);
        DeepSeekRequest apiRequest = new DeepSeekRequest()
                .setModel("deepseek-chat")
                .setMessages(List.of(
                        new DeepSeekRequest.Message()
                                .setRole("user")
                                .setContent(prompt)
                ))
                .setMax_tokens(300)
                .setTemperature(0.3);

        DeepSeekResponse deepSeekResponse = integration.executeRequest(apiRequest);
        return extractTranslation(deepSeekResponse, objectMapper);
    }

    private String buildTranslationPrompt(String word, String language, String targetLanguage) {
        return String.format("""
                        For the word "%s" in %s, provide the following information in JSON format:
                                    
                        {
                          "transcription": "IPA phonetic transcription",
                          "commonTranslations": ["max 2 translations in %s"],
                          "usageExamples": ["max 2 usage examples as complete sentences"],
                          "commonTopics": ["max 2 relevant topics/categories"]
                        }
                                    
                        Requirements:
                        - Translations: Provide exactly 1-2 most common translations
                        - Examples: Natural, complete sentences showing word usage (1-2 examples), sentences must be separated with ';'
                        - Topics: Broad categories where this word is commonly used (1-2 topics)
                        - Keep all responses concise and practical
                        - Use simple, clear language
                                    
                        Word: %s
                        Source language: %s
                        Target language: %s
                        """,
                word,
                language,
                targetLanguage,
                word,
                language,
                targetLanguage
        );
    }
}
