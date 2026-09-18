package ru.olmi.service.telegram.dictionary.topic.search.topic.callback;

public final class UserDictionaryTopicSearchCallback {

    public static final String SEARCH = "dictionary:topic:search";
    public static final String NEXT = "dictionary:topic:search:next";
    public static final String PREVIOUS = "dictionary:topic:search:previous";
    public static final String CURRENT = "dictionary:topic:search:current";

    private UserDictionaryTopicSearchCallback() {
    }

    public static String search() {
        return SEARCH;
    }

    public static boolean isSearch(String callbackData) {
        return SEARCH.equals(callbackData);
    }

    public static String next() {
        return NEXT;
    }

    public static boolean isNext(String callbackData) {
        return NEXT.equals(callbackData);
    }

    public static String previous() {
        return PREVIOUS;
    }

    public static boolean isPrevious(String callbackData) {
        return PREVIOUS.equals(callbackData);
    }

    public static String current() {
        return CURRENT;
    }

    public static boolean isCurrent(String callbackData) {
        return CURRENT.equals(callbackData);
    }
}
