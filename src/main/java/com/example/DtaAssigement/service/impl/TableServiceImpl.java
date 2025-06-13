package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.RestaurantTableDTO;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.RestaurantTable;
import com.example.DtaAssigement.mapper.TableMapper;
import com.example.DtaAssigement.repository.BranchRepository;
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
    private final BranchRepository branchRepo;


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
        Branch branch = branchRepo.findByName(tableDTO.getBranchName())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy chi nhánh: " + tableDTO.getBranchName()));

        RestaurantTable table = TableMapper.toEntity(tableDTO, branch);

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
    public long getTotalTables(String branchName) {
        Branch branch=branchRepo.findByName(branchName).orElseThrow(
                () -> new NoSuchElementException("Không tìm thấy chi nhánh: " + branchName));
        return tableRepo.countByBranch(branch);
    }

    @Override
    public long getAvailableTables(String branchName) {
        Branch branch=branchRepo.findByName(branchName).orElseThrow(
                () -> new NoSuchElementException("Không tìm thấy chi nhánh: " + branchName));
        return tableRepo.countByAvailableTrueAndBranch(branch);
    }

    @Override
    public List<RestaurantTableDTO> getListAvailableTables(String branchName) {
        Branch branch=branchRepo.findByName(branchName).orElseThrow(
                () -> new NoSuchElementException("Không tìm thấy chi nhánh: " + branchName));
        return tableRepo.findByAvailableTrueAndBranch(branch)
                .stream()
                .map(TableMapper::toDTO)
                .collect(Collectors.toList());
    }


}
