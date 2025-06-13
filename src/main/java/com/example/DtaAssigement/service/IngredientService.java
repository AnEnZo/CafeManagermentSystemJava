package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.ImportResultDTO;
import com.example.DtaAssigement.dto.IngredientDTO;
import com.example.DtaAssigement.dto.IngredientStockUpdateDTO;
import com.example.DtaAssigement.entity.Ingredient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IngredientService {
    List<Ingredient> getAll();
    Optional<Ingredient> getById(Long id);
    IngredientDTO create(IngredientDTO ingredientDTO);
    IngredientDTO updateStock(Long id, IngredientStockUpdateDTO dto);
    void delete(Long id);
    ImportResultDTO importFromExcel(MultipartFile file);
}
