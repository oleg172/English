package ru.olmi.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app")
public class AppConfiguration {

    private Integration integrations;
    private TranslationDefaultParams translation;
    private BotProperties bot;

    @Data
    public static class BotProperties {
        private String username;
        private String token;
    }

    @Data
    public static class TranslationDefaultParams {
        private String defaultFromLang;
        private String defaultToLang;
        private boolean defaultPreferUserTranslation;
        private boolean defaultPreferUserTopics;

    }

    @Data
    public static class Integration {
        private IntegrationItem deepseek;
        private IntegrationItem googleTranslate;
    }

    @Data
    public static class IntegrationItem {
        private String host;
        private Map<String, String> paths;
        private String key;
        private Integer timeout;
        private RateLimit rateLimit = new RateLimit();

        @Data
        public static class RateLimit {
            private Integer requestsPerMinute = 40;
            private Long retryDelay = 60000L;
        }

    }
}
