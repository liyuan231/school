package com.school.exception;

public class LikesAlreadyExistException extends Throwable {
    public LikesAlreadyExistException() {
    }

    public LikesAlreadyExistException(String message) {
        super(message);
    }

    public LikesAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public LikesAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public LikesAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
