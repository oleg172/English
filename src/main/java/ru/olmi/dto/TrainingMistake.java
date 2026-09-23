package ru.olmi.dto;

public record TrainingMistake(
        String word,
        String selectedAnswer,
        String correctAnswer
) {
}
