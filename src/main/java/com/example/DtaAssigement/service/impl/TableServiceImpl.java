package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.RestaurantTable;
import com.example.DtaAssigement.repository.TableRepository;
import com.example.DtaAssigement.service.TableService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class TableServiceImpl implements TableService {


    private final TableRepository tableRepo;


    @Override
    @Cacheable(value = "tables", key = "'all'")
    public List<RestaurantTable> getAllTables() {
        return tableRepo.findAll();
    }

    @Override
    @CacheEvict(value = "tables", allEntries = true)
    public RestaurantTable createTable(RestaurantTable table) {

        if (table.getId() != null) {
            // Kiểm tra nếu bảng đã tồn tại
            if (tableRepo.existsById(table.getId())) {
                throw new IllegalStateException("Bàn đã tồn tại với ID: " + table.getId());
            }
        }

        table.setAvailable(true);
        return tableRepo.save(table);
    }

    @Override
    @CacheEvict(value = "tables", allEntries = true)
    public RestaurantTable updateTableStatus(Long id, boolean available) {
        RestaurantTable table = tableRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Table not found with id: " + id));
        table.setAvailable(available);
        return tableRepo.save(table);
    }

    @Override
    @CacheEvict(value = "tables", allEntries = true)
    public boolean deleteTable(Long id){
        if(!tableRepo.existsById(id)){return false;}
        tableRepo.deleteById(id);
        return true;
    }


}
