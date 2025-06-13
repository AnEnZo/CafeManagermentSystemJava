package com.example.DtaAssigement.mapper;


import com.example.DtaAssigement.dto.IngredientDTO;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.Ingredient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IngredientMapper {

    public static IngredientDTO toDTO(Ingredient ingredient) {
        if (ingredient == null) return null;

        return IngredientDTO.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .quantityInStock(ingredient.getQuantityInStock())
                .unit(ingredient.getUnit())
                .importPrice(ingredient.getImportPrice())
                .branchName(ingredient.getBranch().getName())
                .build();
    }

    public static Ingredient toEntity(IngredientDTO dto, Branch branch) {
        if (dto == null) return null;

        return Ingredient.builder()
                .id(dto.getId())
                .name(dto.getName())
                .quantityInStock(dto.getQuantityInStock())
                .unit(dto.getUnit())
                .importPrice(dto.getImportPrice())
                .branch(branch)
                .build();
    }
}

