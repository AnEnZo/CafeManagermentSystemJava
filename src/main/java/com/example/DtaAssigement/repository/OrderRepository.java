package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.dto.ItemStatsDTO;
import com.example.DtaAssigement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
