package ru.olmi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslationResult {
    private Long wordId;
    private Long userWordId;
    private String word;
    private String transcription;
    private List<String> commonTranslations;
    private List<String> userTranslations = new ArrayList<>();
    private List<String> usageExamples;
    private List<String> topics = new ArrayList<>();
    private TranslationSource translationSource;
}
