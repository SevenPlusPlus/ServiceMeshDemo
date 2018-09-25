package com.geely.mesh.demo.orderservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "CommonError:API错误码及描述")
public class CommonError {
    @ApiModelProperty(value = "错误码", required = true)
    private Integer code;
    @ApiModelProperty(value = "错误描述")
    private String cause;
}

