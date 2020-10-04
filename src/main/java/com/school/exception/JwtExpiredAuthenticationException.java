package com.school.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtExpiredAuthenticationException extends AuthenticationException {
    public JwtExpiredAuthenticationException(String msg) {
        super(msg);
    }

    public JwtExpiredAuthenticationException(String message, Throwable t) {
        super(message, t);
    }
}
