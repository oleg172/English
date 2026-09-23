package ru.olmi.dto;

import java.util.List;

public record TrainingQuestionData(
        Long sessionWordId,
        String word,
        String transcription,
        List<String> answers
) {}
