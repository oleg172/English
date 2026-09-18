package ru.olmi.service.telegram.edit.callback;

import ru.olmi.service.telegram.edit.EditWordSource;

public final class EditWordCallback {

    /**
     * Выход из сценария редактирования в главное меню.
     */
    public static final String MENU = "edit_word:menu";

    private static final String WORD_PREFIX = "edit_word:word:";
    private static final String EDIT_PREFIX = "edit_word:edit:";
    private static final String EDIT_MENU_PREFIX = "edit_word:edit_menu:";
    private static final String DELETE_PREFIX = "edit_word:delete:";

    private EditWordCallback() {
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

    public static String edit(Long userWordId, EditWordSource source) {
        return EDIT_PREFIX + source.name().toLowerCase() + ":" + userWordId;
    }

    public static boolean isEdit(String callbackData) {
        return callbackData.startsWith(EDIT_PREFIX);
    }

    public static Long getEditUserWordId(String callbackData) {
        String value = callbackData.substring(EDIT_PREFIX.length());

        int separator = value.indexOf(':');

        return Long.valueOf(value.substring(separator + 1));
    }

    public static EditWordSource getEditSource(String callbackData) {
        String value = callbackData.substring(EDIT_PREFIX.length());

        int separator = value.indexOf(':');

        return EditWordSource.valueOf(value.substring(0, separator).toUpperCase());
    }

    public static String editMenu(Long userWordId) {
        return EDIT_MENU_PREFIX + userWordId;
    }

    public static boolean isEditMenu(String callbackData) {
        return callbackData.startsWith(EDIT_MENU_PREFIX);
    }

    public static String delete(Long userWordId, EditWordSource source) {
        return DELETE_PREFIX + source.name().toLowerCase() + ":" + userWordId;
    }

    public static EditWordSource getDeleteSource(String callbackData) {
        String value = callbackData.substring(DELETE_PREFIX.length());
        int separator = value.indexOf(':');

        return EditWordSource.valueOf(value.substring(0, separator).toUpperCase());
    }

    public static boolean isDelete(String callbackData) {
        return callbackData.startsWith(DELETE_PREFIX);
    }

    public static Long getDeleteUserWordId(String callbackData) {
        String value = callbackData.substring(DELETE_PREFIX.length());
        int separator = value.indexOf(':');

        return Long.valueOf(value.substring(separator + 1));
    }
}
