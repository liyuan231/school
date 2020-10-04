package com.school.exception;

/**
 * 邮箱验证码和缓存中的不一致，用户输错了验证码
 */
public class EmailVerificationCodeIllegalArgumentException extends IllegalArgumentException{
    public EmailVerificationCodeIllegalArgumentException() {
    }

    public EmailVerificationCodeIllegalArgumentException(String s) {
        super(s);
    }

    public EmailVerificationCodeIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailVerificationCodeIllegalArgumentException(Throwable cause) {
        super(cause);
    }
}
