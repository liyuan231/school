package com.school.exception;

/**
 * 邮箱验证码和缓存中的不一致，用户输错了验证码
 */
public class EmailVerificationCodeIllegalArgumentException extends Throwable{
    public EmailVerificationCodeIllegalArgumentException() {
    }

    public EmailVerificationCodeIllegalArgumentException(String message) {
        super(message);
    }

    public EmailVerificationCodeIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailVerificationCodeIllegalArgumentException(Throwable cause) {
        super(cause);
    }

    public EmailVerificationCodeIllegalArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
