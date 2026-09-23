package ru.olmi.dto;

import java.util.List;

public record TrainingQuestion(
        Long userLearningWordId,
        String word,
        String transcription,
        String correctAnswer,
        List<String> answers
) {
}
