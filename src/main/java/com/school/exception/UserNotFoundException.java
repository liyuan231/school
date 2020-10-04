package com.school.exception;

public class UserNotFoundException extends NullPointerException{
    public UserNotFoundException() {
    }

    public UserNotFoundException(String s) {
        super(s);
    }
}
