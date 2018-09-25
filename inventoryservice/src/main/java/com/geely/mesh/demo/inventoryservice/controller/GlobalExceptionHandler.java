package com.geely.mesh.demo.inventoryservice.controller;

import com.geely.mesh.demo.inventoryservice.domain.CommonError;
import com.geely.mesh.demo.inventoryservice.exception.ProductNotAvailableException;
import com.geely.mesh.demo.inventoryservice.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<CommonError> ProductNotFound(ProductNotFoundException e){
        Long productId = e.getProductId();
        CommonError error = new CommonError(20001 , "Product （"+productId+") not found");
        return new ResponseEntity<CommonError>(error, HttpStatus.OK);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<CommonError> ProductNotEnough(ProductNotAvailableException e) {
        CommonError error = new CommonError(20002 , "Product （"+e.getProductId()+") no enough inventory available");
        return new ResponseEntity<CommonError>(error, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonError> defaultErrHandler(Exception e){
        e.printStackTrace();
        CommonError error = new CommonError(10000 , "Unknown internal error");
        return new ResponseEntity<CommonError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
