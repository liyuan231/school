package com.school.exception;

public class UserAdminLoginInCorrectException extends Throwable {
    public UserAdminLoginInCorrectException() {
    }

    public UserAdminLoginInCorrectException(String message) {

    }

    public UserAdminLoginInCorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAdminLoginInCorrectException(Throwable cause) {
        super(cause);
    }

    public UserAdminLoginInCorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
