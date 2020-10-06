package com.school.utils;

public enum  RoleEnum {
    ADMINISTRATOR(1),
    USER(2);
    private int val;

    RoleEnum(int val) {
        this.val = val;
    }

    public int value() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
