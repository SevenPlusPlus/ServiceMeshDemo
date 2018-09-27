package com.geely.mesh.demo.orderservice.service.impl;

import com.geely.mesh.demo.orderservice.domain.Order;
import com.geely.mesh.demo.orderservice.service.OrderService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderServiceImpl implements OrderService {

    static Map<Long, Order> orders = new ConcurrentHashMap<Long, Order>();
    static AtomicLong orderIdCounter = new AtomicLong(0);

    @Override
    public Order createOrder(Long userId, Long productId, Long count, Long totalAmount) {
        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setOrderId(orderIdCounter.addAndGet(1));
        newOrder.setProductId(productId);
        newOrder.setCount(count);
        newOrder.setTotalAmount(totalAmount);
        newOrder.setStatus(0);
        newOrder.setCreateTs(System.currentTimeMillis());
        return newOrder;
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> myorders = Lists.newArrayList();
        for(Order o : orders.values())
        {
            if(o.getUserId() == userId)
            {
                myorders.add(o);
            }
        }
        return myorders;
    }

    @Override
    public Boolean deleteOrderByOrderId(Long orderId) {
        if(!orders.containsKey(orderId)) {
            return false;
        }
        orders.remove(orderId);
        return true;
    }

    @Override
    public Integer changeOrderStatus(Long orderId, Integer newStatus) {
        Integer oldStatus = orders.get(orderId).getStatus();
        orders.get(orderId).setStatus(newStatus);
        return oldStatus;
    }
}
