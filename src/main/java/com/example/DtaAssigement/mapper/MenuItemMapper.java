package com.example.DtaAssigement.mapper;


import com.example.DtaAssigement.dto.MenuItemDTO;

import com.example.DtaAssigement.entity.Category;
import com.example.DtaAssigement.entity.Ingredient;
import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.entity.MenuItemIngredient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        entity.setCategory(category);

        return entity;
    }

}

