package com.school.exception;

/**
 * 邮箱验证码为空，用户没有输入验证码
 */
public class EmailVerificationCodeNullPointerException extends NullPointerException {
    public EmailVerificationCodeNullPointerException(String s) {
        super(s);
    }

    public EmailVerificationCodeNullPointerException() {
    }
}
