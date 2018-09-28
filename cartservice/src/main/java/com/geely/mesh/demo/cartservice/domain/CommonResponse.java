package com.geely.mesh.demo.cartservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CommonResponse:API通用返回结果")
public class CommonResponse {
    @ApiModelProperty(value = "错误码", required = true)
    private Integer code;
    @ApiModelProperty(value = "错误描述")
    private String cause;
    @ApiModelProperty(value = "返回结果值")
    private String result;

    public CommonResponse(Integer code, String cause)
    {
        this.code = code;
        this.cause = cause;
    }

    public CommonResponse(String result)
    {
        this.code = 0;
        this.result = result;
    }

    public CommonResponse()
    {
        this.code = -1;
        this.cause = null;
        this.result = null;
    }
}

