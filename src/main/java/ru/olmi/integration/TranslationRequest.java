package ru.olmi.integration;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TranslationRequest {
    @NotBlank(message = "User is required")
    private String userTelegramId;

    @NotBlank(message = "Word is required")
    private String word;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Target language is required")
    private String targetLanguage;

    private List<String> userTopics;

    private boolean showOnlyUserTopics;

    private List<String> userTranslations;

    private boolean showOnlyUserTranslations;
}
