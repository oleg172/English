package ru.olmi.service.telegram.translate.callback;

public final class TranslationCallback {

    /**
     * добавление слова в словарь пользователя
     */
    public static final String ADD_TO_DICTIONARY = "translation:add";

    /**
     * Возвращение назад в главное меню
     * */
    public static final String MAIN_MENU = "translation:main_menu";

    private TranslationCallback() {
    }

    public static String addToDictionary(Long wordId) {
        return ADD_TO_DICTIONARY + ":" + wordId;
    }

    public static boolean isAddToDictionary(String callbackData) {
        return callbackData.startsWith(ADD_TO_DICTIONARY + ":");
    }

    public static Long getWordId(String callbackData) {
        return Long.valueOf(callbackData.substring((ADD_TO_DICTIONARY + ":").length()));
    }
}
