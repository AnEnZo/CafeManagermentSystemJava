package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableRepository extends JpaRepository<RestaurantTable,Long> {
    RestaurantTable findByName(String name);
    long count(); // Đếm tất cả bàn
    long countByAvailableTrue(); // Đếm số bàn còn trống
    List<RestaurantTable> findByAvailableTrue(); // Lấy danh sách bàn còn trống
    boolean existsByName(String name);
    long countByBranch(Branch branch); // Đếm tất cả bàn theo chi nhánh
    long countByAvailableTrueAndBranch(Branch branch); // Đếm bàn trống theo chi nhánh
    List<RestaurantTable> findByAvailableTrueAndBranch(Branch branch); // Lấy danh sách bàn trống theo chi nhánh
}
