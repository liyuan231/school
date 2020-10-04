package com.school.exception;

public class EmailNotFoundException extends IllegalStateException {
    public EmailNotFoundException() {
    }

    public EmailNotFoundException(String s) {
        super(s);
    }

    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotFoundException(Throwable cause) {
        super(cause);
    }
}
