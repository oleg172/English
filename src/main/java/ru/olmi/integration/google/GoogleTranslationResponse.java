package ru.olmi.integration.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTranslationResponse {

    private List<Sentence> sentences;
    @JsonProperty("alternative_translations")
    private List<AlternativeTranslation> alternativeTranslations;
    private Examples examples;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sentence {
        private String trans;
        private String translit;
        @JsonProperty("src_translit")
        private String srcTranslit;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlternativeTranslation {
        @JsonProperty("src_phrase")
        private String srcPhrase;
        private List<Alternative> alternative;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alternative {
        @JsonProperty("word_postproc")
        private String wordPostproc;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Examples {
        List<Example> example;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Example {
        private String text;
    }
}
