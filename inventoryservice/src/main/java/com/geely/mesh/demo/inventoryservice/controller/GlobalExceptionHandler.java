package com.geely.mesh.demo.inventoryservice.controller;

import com.geely.mesh.demo.inventoryservice.domain.CommonResponse;
import com.geely.mesh.demo.inventoryservice.exception.ProductNotAvailableException;
import com.geely.mesh.demo.inventoryservice.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<CommonResponse> ProductNotFound(ProductNotFoundException e){
        Long productId = e.getProductId();
        CommonResponse error = new CommonResponse(20001 , "Product （"+productId+") not found");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<CommonResponse> ProductNotEnough(ProductNotAvailableException e) {
        CommonResponse error = new CommonResponse(20002 , "Product （"+e.getProductId()+") no enough inventory available");
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> defaultErrHandler(Exception e){
        e.printStackTrace();
        CommonResponse error = new CommonResponse(10000 , "Unknown internal error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
