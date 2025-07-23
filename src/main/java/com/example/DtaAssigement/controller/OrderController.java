package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.OrderCreateDTO;
import com.example.DtaAssigement.entity.Order;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.service.OrderService;
import com.example.DtaAssigement.validation.OnCreate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Order> createOrder(@Validated(OnCreate.class) @RequestBody OrderCreateDTO dto) {
        Order order = orderService.createOrder(dto.getTableId());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/takeaway")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Order> createTakeaway() {
        Order order = orderService.createTakeawayOrder();
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
    public ResponseEntity<Page<Order>> getAllOrders(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "orderTime", // hoặc trường phù hợp
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<Order> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrder(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{orderId}/serve")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Order> serveOrder(@PathVariable Long orderId) {
        Order updated = orderService.updateOrderStatus(orderId, OrderStatus.SERVED);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/latest-by-table/{tableId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Order> getLatestOrderByTable(@PathVariable Long tableId) {
        try {
            Order order = orderService.getLatestOrderByTableId(tableId);
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


//    @PutMapping("/{orderId}/paid")
//    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
//    public ResponseEntity<Order> paidOrder(@PathVariable Long orderId) {
//        Order updated = orderService.updateOrderStatus(orderId, OrderStatus.PAID);
//        return ResponseEntity.ok(updated);
//    }

}
