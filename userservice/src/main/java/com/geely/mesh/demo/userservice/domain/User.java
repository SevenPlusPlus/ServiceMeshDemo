package com.geely.mesh.demo.userservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "User:用户信息")
public class User {
    @ApiModelProperty(value = "UserID")
    private Long userId;
    @ApiModelProperty(value = "用户名", required = true)
    private String name;
    @ApiModelProperty(value = "用户年龄", required = true)
    private Integer age;
    @ApiModelProperty(value = "登录用户名", required = true)
    private String loginName;
    @ApiModelProperty(value = "登录密码")
    private String passwd;
}
