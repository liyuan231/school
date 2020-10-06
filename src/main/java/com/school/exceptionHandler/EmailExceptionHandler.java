package com.school.exceptionHandler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.school.exception.*;
import com.school.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class EmailExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(EmailVerificationCodeIllegalArgumentException.class)
    public String emailVerificationCodeIllegalArgumentException(HttpServletRequest request, Exception e) throws JsonProcessingException {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(EmailVerificationCodeNullPointerException.class)
    public String emailVerificationCodeNullPointerException(HttpServletRequest request, Exception e) throws JsonProcessingException {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }

    @ExceptionHandler(UsernameNullPointerException.class)
    public String usernameNullPointerException(HttpServletRequest request, Exception e) throws JsonProcessingException {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }
    @ExceptionHandler(EmailWrongFormatException.class)
    public String emailWrongFormatException(HttpServletRequest request,Exception e) throws JsonProcessingException {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public String emailNotFoundException(HttpServletRequest request,Exception e) throws JsonProcessingException {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }
}
