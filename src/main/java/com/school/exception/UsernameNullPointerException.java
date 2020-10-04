package com.school.exception;

/**
 * 用户想要获取验证码，然而填错了邮箱号或邮箱格式不正确
 */
public class UsernameNullPointerException extends NullPointerException{
    public UsernameNullPointerException() {
    }

    public UsernameNullPointerException(String s) {
        super(s);
    }
}
