package ru.olmi.service.telegram.edit.translation.callback;

public final class EditWordTranslationCallback {

    /**
     * Добавление пользовательского перевода к слову
     */
    private static final String ADD_PREFIX = "edit_word:add_translation:";
    /**
     * Показ всех пользовательских переводов слова для удаления
     */
    private static final String DELETE_PREFIX = "edit_word:delete_translation:";
    /**
     * Удаление пользовательского перевода
     */
    private static final String REMOVE_PREFIX = "edit_word:remove_translation:";

    private EditWordTranslationCallback() {
    }

    public static String add(Long userWordId) {
        return ADD_PREFIX + userWordId;
    }

    public static boolean isAdd(String callbackData) {
        return callbackData.startsWith(ADD_PREFIX);
    }

    public static Long getAddUserWordId(String callbackData) {
        return Long.valueOf(callbackData.substring(ADD_PREFIX.length()));
    }

    public static String delete(Long userWordId) {
        return DELETE_PREFIX + userWordId;
    }

    public static boolean isDelete(String callbackData) {
        return callbackData.startsWith(DELETE_PREFIX);
    }

    public static Long getDeleteUserWordId(String callbackData) {
        return Long.valueOf(callbackData.substring(DELETE_PREFIX.length()));
    }

    public static String remove(Long userWordId, Long translationId) {
        return REMOVE_PREFIX + userWordId + ":" + translationId;
    }

    public static boolean isRemove(String callbackData) {
        return callbackData.startsWith(REMOVE_PREFIX);
    }

    public static Long getRemoveUserWordId(String callbackData) {
        String value = callbackData.substring(REMOVE_PREFIX.length());

        return Long.valueOf(value.substring(0, value.indexOf(':')));
    }

    public static Long getRemoveTranslationId(String callbackData) {
        String value = callbackData.substring(REMOVE_PREFIX.length());

        return Long.valueOf(value.substring(value.indexOf(':') + 1));
    }
}
