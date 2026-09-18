package ru.olmi.service.integration;

public interface TranslateIntegration {

    Object translate(String word, String language, String targetLanguage);
}
