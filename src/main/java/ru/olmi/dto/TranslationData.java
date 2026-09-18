package ru.olmi.dto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TranslationData {
    private String word;
    private String transcription;
    private List<String> commonTranslations;
    private List<String> userTranslations;
    private List<String> usageExamples;
    private List<String> topics;
    private String language;
    private String translateLanguage;
    private boolean isUserWord;
    private boolean isDBRecord;
}
