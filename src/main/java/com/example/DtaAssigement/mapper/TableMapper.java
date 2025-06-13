package com.example.DtaAssigement.mapper;

import com.example.DtaAssigement.dto.RestaurantTableDTO;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.RestaurantTable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TableMapper {

    // Dùng khi tạo mới: cần truyền đối tượng Branch đã được lấy từ DB
    public static RestaurantTable toEntity(RestaurantTableDTO dto, Branch branch) {
        RestaurantTable table = new RestaurantTable();
        table.setName(dto.getName());
        table.setAvailable(dto.getAvailable());
        table.setCapacity(dto.getCapacity());
        table.setBranch(branch);
        return table;
    }

    public static RestaurantTableDTO toDTO(RestaurantTable table) {
        return RestaurantTableDTO.builder()
                .name(table.getName())
                .available(table.isAvailable())
                .capacity(table.getCapacity())
                .branchName(table.getBranch().getName()) // lấy tên chi nhánh từ entity
                .build();
    }
}
