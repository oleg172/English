package ru.olmi.dto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WordDto {

    private String name;
    private List<String> translations;
    private String transcription;
    private List<String> examples;
    private List<String> topics;
    private List<String> usageExamples;
    private boolean isUserWord;
}
