package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.RestaurantTable;

import java.util.List;

public interface TableService {
    List<RestaurantTable> getAllTables();
    RestaurantTable createTable(RestaurantTable table);
    RestaurantTable updateTableStatus(Long id, boolean available);
    boolean deleteTable(Long id);
}
