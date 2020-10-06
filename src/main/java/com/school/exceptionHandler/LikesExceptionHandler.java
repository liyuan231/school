package com.school.exceptionHandler;

import com.school.exception.LikesAlreadyExistException;
import com.school.exception.LikesNotFoundException;
import com.school.exception.UserLikesNotCorrespondException;
import com.school.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class LikesExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(UserLikesNotCorrespondException.class)
    public String userLikesNotCorrespondException(HttpServletRequest request, Exception e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(LikesNotFoundException.class)
    public String likesNotFoundException(HttpServletRequest request, LikesNotFoundException e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(LikesAlreadyExistException.class)
    public String likesAlreadyExistException(HttpServletRequest request, LikesAlreadyExistException e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
