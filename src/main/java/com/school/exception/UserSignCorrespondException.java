package com.school.exception;

public class UserSignCorrespondException extends Throwable {
    public UserSignCorrespondException() {
    }

    public UserSignCorrespondException(String message) {
        super(message);
    }

    public UserSignCorrespondException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserSignCorrespondException(Throwable cause) {
        super(cause);
    }

    public UserSignCorrespondException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
