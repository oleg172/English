package ru.olmi.dto;

import java.util.List;

public record TrainingAnswerResult(
        boolean correct,
        boolean completed,
        String selectedAnswer,
        String correctAnswer,
        int totalWords,
        int correctAnswers,
        int incorrectAnswers,
        TrainingQuestionData nextQuestion,
        List<TrainingMistake> mistakes
) {
}
