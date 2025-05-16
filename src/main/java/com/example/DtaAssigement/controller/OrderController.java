package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.entity.Order;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;


    // Tạo đơn hàng mới (PENDING)
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Order> createOrder(@RequestParam Long tableId) {
        Order order = orderService.createOrder(tableId);
        return ResponseEntity.ok(order);
    }

    // Thêm món vào đơn hàng
    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<OrderItem> addItem(
            @PathVariable Long orderId,
            @RequestParam Long menuItemId,
            @RequestParam int quantity
    ) {
        OrderItem item = orderService.addItemToOrder(orderId, menuItemId, quantity);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<?> removeItem(
            @PathVariable Long orderId,
            @RequestParam Long menuItemId,
            @RequestParam int quantity
    ) {
        try {
            OrderItem updatedItem = orderService.removeItemFromOrder(orderId, menuItemId, quantity);
            if (updatedItem == null) {
                return ResponseEntity.noContent().build(); // đã xoá item khỏi đơn
            }
            return ResponseEntity.ok(updatedItem); // giảm số lượng còn > 0
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<Order>> getByStatus(@RequestParam OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<Order>> getAllOrder() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@RequestParam Long id) {
        boolean deleted = orderService.deleteOrder(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{orderId}/serve")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Order> serveOrder(@PathVariable Long orderId) {
        Order updated = orderService.updateOrderStatus(orderId, OrderStatus.SERVED);
        return ResponseEntity.ok(updated);
    }


//    @PutMapping("/{orderId}/paid")
//    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
//    public ResponseEntity<Order> paidOrder(@PathVariable Long orderId) {
//        Order updated = orderService.updateOrderStatus(orderId, OrderStatus.PAID);
//        return ResponseEntity.ok(updated);
//    }

}
