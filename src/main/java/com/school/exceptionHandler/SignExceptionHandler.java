package com.school.exceptionHandler;

import com.school.exception.SignAlreadyExistException;
import com.school.exception.SignNotCorrectException;
import com.school.exception.SignNotFoundException;
import com.school.exception.UserSignCorrespondException;
import com.school.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class SignExceptionHandler {
    @ExceptionHandler(SignNotFoundException.class)
    public String signNotFoundException(HttpServletRequest request, SignNotFoundException e) {
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(UserSignCorrespondException.class)
    public String userSignCorrespondException(HttpServletRequest request, UserSignCorrespondException e) {
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    @ExceptionHandler(SignAlreadyExistException.class)
    public String signAlreadyExistException(HttpServletRequest request, SignAlreadyExistException e) {
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    @ExceptionHandler(SignNotCorrectException.class)
    public String signNotCorrectException(HttpServletRequest request, SignNotCorrectException e) {
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}
