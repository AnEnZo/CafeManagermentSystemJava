package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.dto.ItemStatsDTO;
import com.example.DtaAssigement.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT i.menuItem.name AS itemName, SUM(i.quantity) AS quantitySold
            FROM OrderItem i
            GROUP BY i.menuItem.name
            ORDER BY SUM(i.quantity) DESC
            """)
    Page<ItemStatsDTO> findTopSellingItems(org.springframework.data.domain.Pageable topN);

    @Query("SELECT SUM(oi.menuItem.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    double calculateOrderTotalAmount(@Param("orderId") Long orderId);

}