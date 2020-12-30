package com.torn.api.model.exceptions;

public class IncorrectKeyException extends TornApiAccessException {
    public IncorrectKeyException(String message) {
        super(message);
    }

    public IncorrectKeyException() {

    }
}
