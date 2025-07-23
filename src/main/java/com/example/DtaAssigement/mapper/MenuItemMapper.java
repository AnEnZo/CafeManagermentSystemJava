package com.example.DtaAssigement.mapper;


import com.example.DtaAssigement.dto.MenuItemDTO;

import com.example.DtaAssigement.entity.Category;
import com.example.DtaAssigement.entity.MenuItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MenuItemMapper {


    public static MenuItemDTO toDTO(MenuItem entity) {
        if (entity == null) {
            return null;
        }
        return MenuItemDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .category(CategoryMapper.toDTO(entity.getCategory()))
                .build();
    }


    public static MenuItem toEntity(MenuItemDTO dto, Category category) {
        if (dto == null) {
            return null;
        }
        MenuItem entity = new MenuItem();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
        entity.setCategory(category);

        return entity;
    }

}

