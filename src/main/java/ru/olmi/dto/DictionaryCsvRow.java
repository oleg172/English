package ru.olmi.dto;

import java.util.List;

public record DictionaryCsvRow(
        String word,
        List<String> translations,
        List<String> topics
) {
}
