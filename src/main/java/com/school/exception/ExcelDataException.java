package com.school.exception;

public class ExcelDataException extends Throwable{
    public ExcelDataException() {
    }

    public ExcelDataException(String message) {
        super(message);
    }

    public ExcelDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelDataException(Throwable cause) {
        super(cause);
    }

    public ExcelDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
