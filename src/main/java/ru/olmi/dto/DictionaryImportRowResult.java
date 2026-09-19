package ru.olmi.dto;

import ru.olmi.dto.enums.DictionaryImportRowStatus;

public record DictionaryImportRowResult(
        DictionaryImportRowStatus status
) {
}