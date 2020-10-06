package com.school.exception;

public class SignNotFoundException extends Throwable{
    public SignNotFoundException() {
    }

    public SignNotFoundException(String message) {
        super(message);
    }

    public SignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignNotFoundException(Throwable cause) {
        super(cause);
    }

    public SignNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
