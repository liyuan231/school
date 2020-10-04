package com.school.exception;

public class ExcelDataException extends IllegalStateException{
    public ExcelDataException() {
    }

    public ExcelDataException(String s) {
        super(s);
    }

    public ExcelDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelDataException(Throwable cause) {
        super(cause);
    }
}
