package com.school.exceptionHandler;

import com.school.exception.InvalidTokenException;
import com.school.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class JwtExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(InvalidTokenException.class)
    public String invalidTokenException(Exception e, HttpServletRequest request) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }


}
