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

    // Lấy danh sách đơn hàng theo trạng thái
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<Order>> getByStatus(@RequestParam String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
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
