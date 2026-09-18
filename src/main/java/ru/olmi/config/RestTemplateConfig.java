package ru.olmi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate deepSeekRestTemplate(AppConfiguration properties) {
        RestTemplate restTemplate = new RestTemplate();

        // Настройка таймаутов
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getIntegrations().getDeepseek().getTimeout());
        factory.setReadTimeout(properties.getIntegrations().getDeepseek().getTimeout());
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

    @Bean
    public RestTemplate googleRestTemplate(AppConfiguration properties) {
        RestTemplate restTemplate = new RestTemplate();
        var uriBuilderFactory = new DefaultUriBuilderFactory(properties.getIntegrations().getGoogleTranslate().getHost());
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.valueOf("NONE"));
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        // Настройка таймаутов
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getIntegrations().getGoogleTranslate().getTimeout());
        factory.setReadTimeout(properties.getIntegrations().getGoogleTranslate().getTimeout());
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }
}
