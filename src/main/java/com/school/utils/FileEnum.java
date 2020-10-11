package com.school.utils;

public enum FileEnum {
    AVATAR_URL(1),
    LOGO(2),//学校logo
    SIGNATURE(3);//校长签章
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
