package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<RestaurantTable,Long> {
    RestaurantTable findByName(String name);

}
