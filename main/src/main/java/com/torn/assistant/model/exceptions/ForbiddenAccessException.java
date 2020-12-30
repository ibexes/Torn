package com.torn.assistant.model.exceptions;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String msg) {
        super(msg);
    }
}
