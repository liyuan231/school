package com.school.exception;

/**
 * 用户想要获取验证码，然而填错了邮箱号或邮箱格式不正确
 */
public class UsernameNullPointerException extends Throwable{
    public UsernameNullPointerException() {
    }

    public UsernameNullPointerException(String message) {
        super(message);
    }

    public UsernameNullPointerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameNullPointerException(Throwable cause) {
        super(cause);
    }

    public UsernameNullPointerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
