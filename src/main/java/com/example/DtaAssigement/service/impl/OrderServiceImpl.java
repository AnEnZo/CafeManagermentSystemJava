package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.entity.*;
import com.example.DtaAssigement.repository.*;
import com.example.DtaAssigement.security.JwtTokenUtil;
import com.example.DtaAssigement.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepo;
    private final TableRepository tableRepo;
    private final MenuItemRepository menuItemRepo;
    private final OrderItemRepository orderItemRepo;
    private final UserRepository userRepo;
    private final JwtTokenUtil jwtTokenUtil;
    @Override
    public Order createOrder(Long tableId) {
        RestaurantTable table = tableRepo.findById(tableId)
                .orElseThrow(() -> new NoSuchElementException("Table not found: " + tableId));

        if (!table.isAvailable()) {
            throw new IllegalStateException("Table " + tableId + " đã có người !");
        }

        String nameStaff =jwtTokenUtil.getCurrentUsername();
        User staff = userRepo.findByUsername(nameStaff)
                .orElseThrow(() -> new IllegalStateException("Staff not found: " + nameStaff));

        Order order = Order.builder()
                .table(table)
                .orderTime(LocalDateTime.now())
                .staff(staff)
                .status(OrderStatus.PENDING)
                .build();
        table.setAvailable(false);
        tableRepo.save(table);
        return orderRepo.save(order);
    }

    @Override
    public OrderItem addItemToOrder(Long orderId, Long menuItemId, int quantity) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        MenuItem item = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new NoSuchElementException("MenuItem not found: " + menuItemId));
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menuItem(item)
                .quantity(quantity)
                .build();
        order.getOrderItems().add(orderItem);
        return orderItemRepo.save(orderItem);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepo.findAll().stream()
                .filter(o -> o.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }

    public boolean deleteOrder(Long id){
        if(!orderRepo.existsById(id)){return false;}
        orderRepo.deleteById(id);
        return true;
    }
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        order.setStatus(status);
        if(status == OrderStatus.PAID) {
            RestaurantTable table = order.getTable();
            table.setAvailable(true);
            tableRepo.save(table);
        }
        return orderRepo.save(order);
    }

}
