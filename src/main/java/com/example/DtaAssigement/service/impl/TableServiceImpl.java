package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.RestaurantTableDTO;
import com.example.DtaAssigement.entity.RestaurantTable;
import com.example.DtaAssigement.mapper.TableMapper;
import com.example.DtaAssigement.repository.TableRepository;
import com.example.DtaAssigement.service.TableService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TableServiceImpl implements TableService {


    private final TableRepository tableRepo;

    @Override
    @Cacheable(value = "tables", key = "'all'")
    public List<RestaurantTableDTO> getAllTables() {
        return tableRepo.findAll()
                .stream()
                .map(TableMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    @CacheEvict(value = "tables", allEntries = true)
    public RestaurantTableDTO createTable(RestaurantTableDTO tableDTO) {
        if (tableDTO.getName() != null) {
            // Kiểm tra nếu bảng đã tồn tại
            if (tableRepo.existsByName(tableDTO.getName())) {
                throw new IllegalStateException("Bàn đã tồn tại với ID: " + tableDTO.getName());
            }
        }


        RestaurantTable table = TableMapper.toEntity(tableDTO);

        table.setAvailable(true); // mặc định là true khi tạo mới

        RestaurantTable created = tableRepo.save(table);

        return TableMapper.toDTO(created);
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




    @Override
    public List<RestaurantTableDTO> getListAvailableTables() {
        return tableRepo.findByAvailableTrue()
                .stream()
                .map(TableMapper::toDTO)
                .collect(Collectors.toList());
    }


}
