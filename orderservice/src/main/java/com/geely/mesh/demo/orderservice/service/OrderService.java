package com.geely.mesh.demo.orderservice.service;

import com.geely.mesh.demo.orderservice.domain.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, Long productId, Long count, Long totalAmount);

    List<Order> getOrdersByUserId(Long userId);

    Boolean deleteOrderByOrderId(Long orderId);

    Integer changeOrderStatus(Long orderId, Integer newStatus);
}
