package ru.olmi.service.telegram.dictionary.topic.callback;

public final class UserDictionaryTopicCallback {
    public static final String MENU = "dictionary:topic:menu";
    public static final String TOPICS = "dictionary:topic:list";
    public static final String TOPIC_PREFIX = "dictionary:topic:id";
    public static final String NEXT = "dictionary:topic:next";
    public static final String PREVIOUS = "dictionary:topic:previous";
    public static final String CURRENT = "dictionary:topic:current";

    public static final String WORD_PREFIX = "dictionary:topic:word:id";
    public static final String WORD_NEXT = "dictionary:topic:word:next";
    public static final String WORD_PREVIOUS = "dictionary:topic:word:previous";
    public static final String WORD_CURRENT = "dictionary:topic:word:current";

    private UserDictionaryTopicCallback() {
    }

    public static String menu() {
        return MENU;
    }

    public static String topics() {
        return TOPICS;
    }

    public static String topic(Long topicId) {
        return TOPIC_PREFIX + topicId;
    }

    public static boolean isTopic(String callbackData) {
        return callbackData.startsWith(TOPIC_PREFIX)
                && !NEXT.equals(callbackData)
                && !PREVIOUS.equals(callbackData)
                && !CURRENT.equals(callbackData);
    }

    public static Long getTopicId(String callbackData) {
        return Long.valueOf(callbackData.substring(TOPIC_PREFIX.length()));
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

    public static boolean isTopics(String callbackData) {
        return TOPICS.equals(callbackData);
    }

    public static boolean isMenu(String callbackData) {
        return MENU.equals(callbackData);
    }

    public static boolean isNext(String callbackData) {
        return NEXT.equals(callbackData);
    }

    public static boolean isPrevious(String callbackData) {
        return PREVIOUS.equals(callbackData);
    }

    public static boolean isCurrent(String callbackData) {
        return CURRENT.equals(callbackData);
    }

    public static String word(Long userWordId) {
        return WORD_PREFIX + userWordId;
    }

    public static boolean isWord(String callbackData) {
        return callbackData.startsWith(WORD_PREFIX)
                && !WORD_NEXT.equals(callbackData)
                && !WORD_PREVIOUS.equals(callbackData)
                && !WORD_CURRENT.equals(callbackData);
    }

    public static Long getUserWordId(String callbackData) {
        return Long.valueOf(callbackData.substring(WORD_PREFIX.length()));
    }

    public static String wordNext() {
        return WORD_NEXT;
    }

    public static String wordPrevious() {
        return WORD_PREVIOUS;
    }

    public static String wordCurrent() {
        return WORD_CURRENT;
    }

    public static boolean isWordNext(String callbackData) {
        return WORD_NEXT.equals(callbackData);
    }

    public static boolean isWordPrevious(String callbackData) {
        return WORD_PREVIOUS.equals(callbackData);
    }

    public static boolean isWordCurrent(String callbackData) {
        return WORD_CURRENT.equals(callbackData);
    }
}