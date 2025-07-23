package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryName(String categoryName);
}
