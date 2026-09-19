package ru.olmi.dto;

import java.util.List;

public record DictionaryImportRow(
        String word,
        List<String> translations,
        List<String> topics
) {
}
