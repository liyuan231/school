package com.school.exceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.school.exception.FileFormattingException;
import com.school.utils.ResponseUtil;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class FileExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(FileFormattingException.class)
    public String fileFormattingException(HttpServletRequest request,
                                          FileFormattingException e) {
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    @ExceptionHandler(MissingServletRequestPartException.class)
    public String missingServletRequestPartException(HttpServletRequest request,MissingServletRequestPartException e){
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public String fileSizeLimitExceededException(HttpServletRequest request,MissingServletRequestPartException e){
        logger.info("[" + request.getRemoteAddr() + "] ERROR " + e.getMessage());
        return ResponseUtil.build(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
