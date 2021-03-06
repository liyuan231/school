package com.school.exceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.school.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class CommonExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentException(HttpServletRequest request, Exception e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public String nullPointerException(HttpServletRequest request, Exception e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }


}
