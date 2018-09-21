package com.geely.mesh.demo.userservice.controller;

import com.geely.mesh.demo.userservice.domain.CommonError;
import com.geely.mesh.demo.userservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonError> UserNotFound(UserNotFoundException e){
        Long userId = e.getUserId();
        CommonError error = new CommonError(10001 , "User （"+userId+") not found");
        return new ResponseEntity<CommonError>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonError> defaultErrHandler(Exception e){
        e.printStackTrace();
        CommonError error = new CommonError(10000 , "Unknown internal error");
        return new ResponseEntity<CommonError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
