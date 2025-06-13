package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.RestaurantTableDTO;
import com.example.DtaAssigement.entity.RestaurantTable;

import java.util.List;

public interface TableService {
    List<RestaurantTableDTO> getAllTables();
    RestaurantTableDTO createTable(RestaurantTableDTO tableDTO);
    RestaurantTable updateTableStatus(Long id, boolean available);
    boolean deleteTable(Long id);
    long getTotalTables(String branchName);
    long getAvailableTables(String branchName);
    List<RestaurantTableDTO> getListAvailableTables(String branchName);
}
