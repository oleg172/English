package ru.olmi.service.telegram.learning.topic.callback;

public final class LearningTopicCallback {

    public static final String TOPICS = "learning:topic:list";
    public static final String TOPIC_PREFIX = "learning:topic:id";
    public static final String NEXT = "learning:topic:next";
    public static final String PREVIOUS = "learning:topic:previous";
    public static final String CURRENT = "learning:topic:current";
    public static final String MENU = "learning:topic:menu";

    private LearningTopicCallback() {
    }

    public static String topics() {
        return TOPICS;
    }

    public static String topic(Long topicId) {
        return TOPIC_PREFIX + topicId;
    }

    public static boolean isTopic(String callbackData) {
        return callbackData.startsWith(TOPIC_PREFIX);
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

    public static String menu() {
        return MENU;
    }

    public static boolean isTopics(String callbackData) {
        return TOPICS.equals(callbackData);
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

    public static boolean isMenu(String callbackData) {
        return MENU.equals(callbackData);
    }
}
