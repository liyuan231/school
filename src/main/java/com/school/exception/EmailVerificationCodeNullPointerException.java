package com.school.exception;

/**
 * 邮箱验证码为空，用户没有输入验证码
 */
public class EmailVerificationCodeNullPointerException extends Throwable {
    public EmailVerificationCodeNullPointerException() {
    }

    public EmailVerificationCodeNullPointerException(String message) {
        super(message);
    }

    public EmailVerificationCodeNullPointerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailVerificationCodeNullPointerException(Throwable cause) {
        super(cause);
    }

    public EmailVerificationCodeNullPointerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
