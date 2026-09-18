package ru.olmi.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.olmi.dto.TranslationResult;

@Component
public class TranslationMessageFormatter {

    public String format(TranslationResult result) {
        StringBuilder message = new StringBuilder();

        appendWord(message, result);
        appendTranscription(message, result);
        appendTranslations(message, result);
        appendExamples(message, result);
        appendTopics(message, result);

        return message.toString();
    }

    private void appendWord(
            StringBuilder message,
            TranslationResult result
    ) {
        message.append("🔎 ")
               .append(result.getWord())
               .append("\n");
    }

    private void appendTranscription(
            StringBuilder message,
            TranslationResult result
    ) {
        if (isEmpty(result.getTranscription())) {
            return;
        }

        message.append("\n")
               .append("🔊 ")
               .append(result.getTranscription())
               .append("\n");
    }

    private void appendTranslations(
            StringBuilder message,
            TranslationResult result
    ) {
        List<String> userTranslations = safeList(
                result.getUserTranslations()
        );

        List<String> commonTranslations = safeList(
                result.getCommonTranslations()
        );

        Set<String> userTranslationSet = userTranslations.stream()
                                                         .map(this::normalize)
                                                         .collect(Collectors.toSet());

        List<String> uniqueCommonTranslations = commonTranslations
                .stream()
                .filter(translation -> !userTranslationSet.contains(normalize(translation)))
                .distinct()
                .toList();

        if (userTranslations.isEmpty() && uniqueCommonTranslations.isEmpty()) {
            return;
        }

        message.append("\n")
               .append("Перевод:\n");

        for (String translation : userTranslations) {
            message.append("👤 ")
                   .append(translation)
                   .append("\n");
        }

        for (String translation : uniqueCommonTranslations) {
            message.append("• ")
                   .append(translation)
                   .append("\n");
        }
    }

    private void appendExamples(
            StringBuilder message,
            TranslationResult result
    ) {
        List<String> examples = safeList(result.getUsageExamples());

        if (examples.isEmpty()) {
            return;
        }

        message.append("\n")
               .append("Примеры:\n");

        for (String example : examples) {
            message.append("• ")
                   .append(example)
                   .append("\n");
        }
    }

    private void appendTopics(
            StringBuilder message,
            TranslationResult result
    ) {
        List<String> topics = safeList(result.getTopics());

        if (topics.isEmpty()) {
            return;
        }

        message.append("\n")
               .append("Топики:\n")
               .append("🏷 ")
               .append(String.join(", ", topics))
               .append("\n");
    }

    private List<String> safeList(List<String> values) {
        return values == null
               ? Collections.emptyList()
               : values.stream()
                       .filter(value -> value != null && !value.isBlank())
                       .toList();
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }

    private boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }
}