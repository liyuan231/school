package com.school.exception;

public class EmailWrongFormatException extends IllegalArgumentException{
    public EmailWrongFormatException() {
    }

    public EmailWrongFormatException(String s) {
        super(s);
    }

    public EmailWrongFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailWrongFormatException(Throwable cause) {
        super(cause);
    }
}
