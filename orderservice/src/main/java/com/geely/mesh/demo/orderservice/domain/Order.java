package com.geely.mesh.demo.orderservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Order:订单信息")
public class Order {
    @ApiModelProperty(value = "订单号")
    private Long orderId;
    @ApiModelProperty(value = "订单状态", required = true)
    private Integer status; /*0 == init, 1 == pay success, 2 == pay failed, 3 == stock-out success, 4 == stock-out failed*/
    @ApiModelProperty(value = "商品ID", required = true)
    private Long productId;
    @ApiModelProperty(value = "订购商品数量", required = true)
    private Long count;
    @ApiModelProperty(value = "商品总额", required = true)
    private Long totalAmount;
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;
    @ApiModelProperty(value = "创建时间", required = true)
    private Long createTs;
}
