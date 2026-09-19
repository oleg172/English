package ru.olmi.exception;

public class DictionaryImportException extends RuntimeException {

    public DictionaryImportException(String message) {
        super(message);
    }

    public DictionaryImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
