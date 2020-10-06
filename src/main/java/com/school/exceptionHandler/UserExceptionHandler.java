package com.school.exceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.school.exception.UserNotCorrectException;
import com.school.exception.UserNotFoundException;
import com.school.exception.UsernameAlreadyExistException;
import com.school.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class UserExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(UserNotFoundException.class)
    public String userNotFoundException(HttpServletRequest request,
                                        UserNotFoundException e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());

    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String usernameAlreadyExistException(HttpServletRequest request,
                                                UsernameAlreadyExistException e){
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }
    @ExceptionHandler(UserNotCorrectException.class)
    public String userNotCorrectException(HttpServletRequest request,
                                          UserNotCorrectException e){
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(),  e.getMessage());
    }

}
