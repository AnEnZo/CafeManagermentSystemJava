package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.Order;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.ennum.OrderStatus;

import java.util.List;

public interface OrderService {
    Order createOrder(Long tableId);
    OrderItem addItemToOrder(Long orderId, Long menuItemId, int quantity);
    List<Order> getOrdersByStatus(String status);
    boolean deleteOrder(Long id);
    Order updateOrderStatus(Long orderId, OrderStatus status);
}
