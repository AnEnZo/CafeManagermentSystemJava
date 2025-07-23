package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.dto.ItemStatsDTO;
import com.example.DtaAssigement.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.DtaAssigement.ennum.OrderStatus;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT i.menuItem.name AS itemName,
               SUM(i.quantity) AS quantitySold,
               i.menuItem.price AS price,
               i.menuItem.imageUrl AS imageUrl
        FROM OrderItem i
        JOIN i.order o
        WHERE o.status = OrderStatus.PAID
        GROUP BY i.menuItem.name, i.menuItem.price, i.menuItem.imageUrl
        ORDER BY SUM(i.quantity) DESC
        """)
    Page<ItemStatsDTO> findTopSellingItems(Pageable topN);

    @Query("SELECT SUM(oi.menuItem.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal calculateOrderTotalAmount(@Param("orderId") Long orderId);

    Optional<OrderItem> findByOrderIdAndMenuItemId(Long orderId, Long menuItemId);


}