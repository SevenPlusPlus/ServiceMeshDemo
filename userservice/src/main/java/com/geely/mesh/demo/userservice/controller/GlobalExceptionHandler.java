package com.geely.mesh.demo.userservice.controller;

import com.geely.mesh.demo.userservice.domain.CommonResponse;
import com.geely.mesh.demo.userservice.exception.UserLoginFailedException;
import com.geely.mesh.demo.userservice.exception.UserNoEnoughBalanceException;
import com.geely.mesh.demo.userservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonResponse> UserNotFound(UserNotFoundException e){
        Long userId = e.getUserId();
        CommonResponse error = new CommonResponse(10001 , "User （"+userId+") not found");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(UserLoginFailedException.class)
    public ResponseEntity<CommonResponse> UserLoginFailed(UserLoginFailedException e){
        CommonResponse error = new CommonResponse(10001 , "User （"+e.getLoginName()+") login failed");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(UserNoEnoughBalanceException.class)
    public ResponseEntity<CommonResponse> UserPaymentFailed(UserNoEnoughBalanceException e) {
        CommonResponse error = new CommonResponse(10002 , "User （"+e.getUserId()+") no enough money available");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> defaultErrHandler(Exception e){
        e.printStackTrace();
        CommonResponse error = new CommonResponse(10000 , "Unknown internal error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
