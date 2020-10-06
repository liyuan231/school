package com.school.exception;

public class SignNotCorrectException extends Throwable {

    public SignNotCorrectException() {
    }

    public SignNotCorrectException(String message) {
        super(message);
    }

    public SignNotCorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignNotCorrectException(Throwable cause) {
        super(cause);
    }

    public SignNotCorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
