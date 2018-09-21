package com.geely.mesh.demo.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLoginFailedException extends RuntimeException{
    private String loginName;
}
