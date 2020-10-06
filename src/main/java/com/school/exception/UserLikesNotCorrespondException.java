package com.school.exception;

public class UserLikesNotCorrespondException extends Throwable {
    public UserLikesNotCorrespondException(String s) {
    }

    public UserLikesNotCorrespondException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserLikesNotCorrespondException(Throwable cause) {
        super(cause);
    }

    public UserLikesNotCorrespondException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserLikesNotCorrespondException() {
    }
}
