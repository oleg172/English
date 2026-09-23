package ru.olmi.service.telegram.learning.topic.selection.callback;

public final class TrainingCallback {
    public static final String ANSWER_PREFIX = "training:answer:";
    public static final String MENU = "training:menu";

    private TrainingCallback() {
    }

    public static String answer(Long sessionWordId, int answerIndex) {
        return ANSWER_PREFIX + sessionWordId + ":" + answerIndex;
    }

    public static boolean isAnswer(String callbackData) {
        return callbackData.startsWith(ANSWER_PREFIX);
    }

    public static Long getSessionWordId(String callbackData) {
        String value = callbackData.substring(ANSWER_PREFIX.length());
        return Long.parseLong(value.substring(0, value.indexOf(':')));
    }

    public static int getAnswerIndex(String callbackData) {
        String value = callbackData.substring(ANSWER_PREFIX.length());
        return Integer.parseInt(value.substring(value.indexOf(':') + 1));
    }

    public static String menu() {
        return MENU;
    }

    public static boolean isMenu(String callbackData) {
        return MENU.equals(callbackData);
    }
}
