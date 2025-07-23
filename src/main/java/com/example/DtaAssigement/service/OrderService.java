package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.Order;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.ennum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrder(Long tableId);
    OrderItem addItemToOrder(Long orderId, Long menuItemId, int quantity);
    OrderItem removeItemFromOrder(Long orderId, Long menuItemId, int quantityToRemove);
    List<Order> getOrdersByStatus(OrderStatus status);
    Page<Order> getAllOrders(Pageable pageable);
    boolean deleteOrder(Long id);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    Order createTakeawayOrder();
    Order getOrderById(Long id);
    Order getLatestOrderByTableId(Long tableId);

}
