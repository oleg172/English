package ru.olmi.service.integration;

import java.net.URI;
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
import org.springframework.web.util.UriComponentsBuilder;
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

        String path = configuration.getIntegrations().getGoogleTranslate().getHost()
                + configuration.getIntegrations().getGoogleTranslate().getPaths().get("TRANSLATE");
        URI uri = UriComponentsBuilder.fromUriString(path)
                                      .buildAndExpand(
                                              language.toLowerCase().substring(0, 2),
                                              targetLanguage.toLowerCase().substring(0, 2),
                                              word
                                      )
                                      .encode()
                                      .toUri();
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<GoogleTranslationResponse> response = googleRestTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                GoogleTranslationResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Google API returned error: " + response.getStatusCode());
        }
    }
}
