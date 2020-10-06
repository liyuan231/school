package com.school.exception;

public class EmailWrongFormatException extends Throwable{
    public EmailWrongFormatException() {
    }

    public EmailWrongFormatException(String message) {
        super(message);
    }

    public EmailWrongFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailWrongFormatException(Throwable cause) {
        super(cause);
    }

    public EmailWrongFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
