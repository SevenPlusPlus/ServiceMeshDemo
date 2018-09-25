package com.geely.mesh.demo.inventoryservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Inventory:库存信息")
public class Inventory {
    @ApiModelProperty(value = "商品ID")
    private Long productId;
    @ApiModelProperty(value = "商品名称", required = true)
    private String productName;
    @ApiModelProperty(value = "商品单价", required = true)
    private Long price;
    @ApiModelProperty(value = "当前库存存量", required = true)
    private Long availableNum;
    @ApiModelProperty(value = "创建时间")
    private Long createTs;
    @ApiModelProperty(value = "更新时间")
    private Long updateTs;
}
