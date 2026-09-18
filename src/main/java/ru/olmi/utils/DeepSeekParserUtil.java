package ru.olmi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.olmi.dto.TranslationResult;
import ru.olmi.integration.deepseek.DeepSeekResponse;

@UtilityClass
@Slf4j
public class DeepSeekParserUtil {

    public static TranslationResult extractTranslation(DeepSeekResponse response, ObjectMapper objectMapper) {
        try {
            String content = response.getChoices().get(0).getMessage().getContent();
            String jsonString = extractJsonFromResponse(content);

            return objectMapper.readValue(jsonString, TranslationResult.class);
        } catch (Exception e) {
            log.warn("Failed to parse JSON response, falling back to text parsing: {}", e.getMessage());
            return fallbackTextParsing(response.getChoices().get(0).getMessage().getContent());
        }
    }

    private String extractJsonFromResponse(String content) {
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}") + 1;

        if (start >= 0 && end > start) {
            return content.substring(start, end);
        }

        throw new IllegalArgumentException("No JSON found in response");
    }

    private TranslationResult fallbackTextParsing(String content) {
        TranslationResult data = new TranslationResult();

        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.toLowerCase();
            if (line.contains("transcription:") || line.contains("ipa:")) {
                data.setTranscription(extractValue(line));
            } else if (line.contains("translations")) {
                data.setCommonTranslations(extractList(line, 2));
            } else if (line.contains("example") || line.contains("usage")) {
                data.setUsageExamples(extractList(line, 2));
            } else if (line.contains("topics") || line.contains("category")) {
                //data.setCommonTopics(extractList(line, 2));
            }
        }
        return data;
    }

    private String extractValue(String line) {
        String[] parts = line.split(":", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    private List<String> extractList(String line, int maxItems) {
        String value = extractValue(line);
        return Arrays.stream(value.split(","))
                     .limit(maxItems)
                     .map(String::trim)
                     .collect(Collectors.toList());
    }
}
