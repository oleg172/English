package ru.olmi.service.integration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.olmi.config.AppConfiguration;
import ru.olmi.integration.deepseek.DeepSeekRequest;
import ru.olmi.integration.deepseek.DeepSeekResponse;

@Service
@AllArgsConstructor
@Slf4j
public class DeepSeekIntegration {

    private final RestTemplate deepSeekRestTemplate;
    private final AppConfiguration config;

    private static final String COMPLETIONS_PATH = "COMPLETIONS";

    @Retryable(value = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public DeepSeekResponse executeRequest(DeepSeekRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getIntegrations().getDeepseek().getKey());

            HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<DeepSeekResponse> response = deepSeekRestTemplate.exchange(
                    config.getIntegrations().getDeepseek().getHost() + config.getIntegrations().getDeepseek().getPaths().getOrDefault(COMPLETIONS_PATH, ""),
                    HttpMethod.POST,
                    entity,
                    DeepSeekResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("DeepSeek API returned error: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Rate limit exceeded, waiting {} ms", config.getIntegrations().getDeepseek().getRateLimit().getRetryDelay());
            try {
                Thread.sleep(config.getIntegrations().getDeepseek().getRateLimit().getRetryDelay());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            throw e;
        }
    }
}
