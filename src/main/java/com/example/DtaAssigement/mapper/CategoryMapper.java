package com.example.DtaAssigement.mapper;

import com.example.DtaAssigement.dto.CategoryDTO;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public static CategoryDTO toDTO(Category entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .branchName(entity.getBranch().getName())
                .build();
    }

    public static Category toEntity(CategoryDTO dto, Branch branch) {
        Category category = Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .branch(branch)
                .build();
        return category;
    }
}
