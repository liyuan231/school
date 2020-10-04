package com.school.exception;

public class FileFormattingException extends IllegalArgumentException {
    public FileFormattingException() {
    }

    public FileFormattingException(String s) {
        super(s);
    }

    public FileFormattingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormattingException(Throwable cause) {
        super(cause);
    }
}
