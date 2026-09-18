package ru.olmi.service.telegram.dictionary.allwords.callback;

public final class UserDictionaryCallback {
    public static final String MENU = "dictionary:menu";
    public static final String LIST = "dictionary:list";
    public static final String TOPICS = "dictionary:topics";
    public static final String MAIN_MENU = "dictionary:main_menu";
    public static final String WORD_PREFIX = "dictionary:word:";
    public static final String NEXT = "dictionary:next";
    public static final String PREVIOUS = "dictionary:previous";
    public static final String BACK = "dictionary:back";
    public static final String CURRENT = "dictionary:current";

    private UserDictionaryCallback() {
    }

    public static String word(Long userWordId) {
        return WORD_PREFIX + userWordId;
    }

    public static boolean isWord(String callbackData) {
        return callbackData.startsWith(WORD_PREFIX);
    }

    public static Long getUserWordId(String callbackData) {
        return Long.valueOf(callbackData.substring(WORD_PREFIX.length()));
    }

    public static boolean isNext(String callbackData) {
        return NEXT.equals(callbackData);
    }

    public static boolean isPrevious(String callbackData) {
        return PREVIOUS.equals(callbackData);
    }

    public static boolean isBack(String callbackData) {
        return BACK.equals(callbackData);
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

    public static boolean isCurrent(String callbackData) {
        return CURRENT.equals(callbackData);
    }
}