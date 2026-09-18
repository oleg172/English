package ru.olmi.integration;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TranslationResponse {
    private String word;
    private String sourceLanguage;
    private String targetLanguage;
    private String transcription;
    private List<String> translations;
    private List<String> usageExamples;
    private List<String> topics;
}
