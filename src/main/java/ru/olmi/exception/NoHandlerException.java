package ru.olmi.exception;

import lombok.Getter;

@Getter
public class NoHandlerException extends RuntimeException {

    public NoHandlerException(Object selector) {
        super(selector.toString());
    }
}
