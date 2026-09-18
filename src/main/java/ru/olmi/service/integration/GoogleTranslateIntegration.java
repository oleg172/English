package ru.olmi.service.integration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.olmi.config.AppConfiguration;
import ru.olmi.integration.google.GoogleTranslationResponse;

@Service
@AllArgsConstructor
@Slf4j
public class GoogleTranslateIntegration implements TranslateIntegration {

    private final RestTemplate googleRestTemplate;
    private final AppConfiguration configuration;

    @Override
    @Retryable(value = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public GoogleTranslationResponse translate(String word, String language, String targetLanguage) {
        log.info("Try to find translation via Google translate service for word [{}], language [{}], targetLanguage [{}]", word, language, targetLanguage);

        HttpEntity<Void> requestEntity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<GoogleTranslationResponse> response = googleRestTemplate.exchange(
                configuration.getIntegrations().getGoogleTranslate().getPaths().get("TRANSLATE"),
                HttpMethod.POST,
                requestEntity,
                GoogleTranslationResponse.class,
                language.toLowerCase().substring(0, 2),
                targetLanguage.toLowerCase().substring(0, 2),
                word
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Google API returned error: " + response.getStatusCode());
        }
    }
}
