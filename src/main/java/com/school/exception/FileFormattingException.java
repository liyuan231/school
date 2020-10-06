package com.school.exception;

public class FileFormattingException extends Throwable {
    public FileFormattingException() {
    }

    public FileFormattingException(String message) {
        super(message);
    }

    public FileFormattingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormattingException(Throwable cause) {
        super(cause);
    }

    public FileFormattingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
