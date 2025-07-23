package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.dto.ItemStatsDTO;
import com.example.DtaAssigement.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findById(@Param("id") Long id);

    Optional<Order> findTopByTableIdOrderByOrderTimeDesc(Long tableId);

}
