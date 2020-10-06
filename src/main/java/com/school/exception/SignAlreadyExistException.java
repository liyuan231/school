package com.school.exception;

public class SignAlreadyExistException extends Throwable {
    public SignAlreadyExistException() {
    }

    public SignAlreadyExistException(String message) {
        super(message);
    }

    public SignAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public SignAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
