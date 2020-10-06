package com.school.utils;

public enum  FileEnum {
    AVATAR_URL(1);
    private int code;

    FileEnum(int code) {
        this.code = code;
    }

    public int value() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
