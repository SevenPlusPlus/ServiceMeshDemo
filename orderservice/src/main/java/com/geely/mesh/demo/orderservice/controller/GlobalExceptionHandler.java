package com.geely.mesh.demo.orderservice.controller;

import com.geely.mesh.demo.orderservice.domain.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> defaultErrHandler(Exception e){
        e.printStackTrace();
        CommonResponse error = new CommonResponse(10000 , e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
