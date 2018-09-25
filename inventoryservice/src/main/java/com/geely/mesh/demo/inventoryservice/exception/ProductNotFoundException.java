package com.geely.mesh.demo.inventoryservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductNotFoundException extends RuntimeException{
    private Long productId;
}
