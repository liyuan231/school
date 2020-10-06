package com.school.exception;

public class LikesNotFoundException extends Throwable{
    public LikesNotFoundException() {
    }

    public LikesNotFoundException(String message) {
        super(message);
    }

    public LikesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LikesNotFoundException(Throwable cause) {
        super(cause);
    }

    public LikesNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
