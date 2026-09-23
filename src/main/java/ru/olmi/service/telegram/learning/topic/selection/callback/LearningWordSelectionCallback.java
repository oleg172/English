package ru.olmi.service.telegram.learning.topic.selection.callback;

public final class LearningWordSelectionCallback {
    public static final String WORD_PREFIX = "learning:words:id";
    public static final String NEXT = "learning:words:next";
    public static final String PREVIOUS = "learning:words:previous";
    public static final String CURRENT = "learning:words:current";
    public static final String CONFIRM = "learning:words:confirm";
    public static final String BACK = "learning:words:back";

    private LearningWordSelectionCallback() {}

    public static String word(Long userWordId) {
        return WORD_PREFIX + userWordId;
    }

    public static boolean isWord(String callbackData) {
        return callbackData.startsWith(WORD_PREFIX);
    }

    public static Long getUserWordId(String callbackData) {
        return Long.valueOf(callbackData.substring(WORD_PREFIX.length()));
    }

    public static String next() {
        return NEXT;
    }

    public static String previous() {
        return PREVIOUS;
    }

    public static String current() {
        return CURRENT;
    }

    public static String confirm() {
        return CONFIRM;
    }

    public static String back() {
        return BACK;
    }

    public static boolean isNext(String data) {
        return NEXT.equals(data);
    }

    public static boolean isPrevious(String data) {
        return PREVIOUS.equals(data);
    }

    public static boolean isCurrent(String data) {
        return CURRENT.equals(data);
    }

    public static boolean isConfirm(String data) {
        return CONFIRM.equals(data);
    }

    public static boolean isBack(String data) {
        return BACK.equals(data);
    }
}
