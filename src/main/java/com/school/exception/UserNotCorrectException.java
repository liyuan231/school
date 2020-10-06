package com.school.exception;

public class UserNotCorrectException extends Throwable {
    public UserNotCorrectException() {
    }

    public UserNotCorrectException(String message) {
        super(message);
    }

    public UserNotCorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotCorrectException(Throwable cause) {
        super(cause);
    }

    public UserNotCorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
