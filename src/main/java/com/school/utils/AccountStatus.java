package com.school.utils;

public enum  AccountStatus {

    ALLOW_LOGIN(1),
    NOT_ALLOW_LOGIN(0);
    private int value;

    AccountStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
