package com.example.DtaAssigement.controller;


import com.example.DtaAssigement.dto.ImportResultDTO;
import com.example.DtaAssigement.dto.IngredientDTO;
import com.example.DtaAssigement.dto.IngredientStockUpdateDTO;
import com.example.DtaAssigement.entity.Ingredient;
import com.example.DtaAssigement.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<Ingredient> getAll() {
        return ingredientService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public Ingredient getById(@PathVariable Long id) {
        return ingredientService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu với id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public IngredientDTO importIngredient(@Valid @RequestBody IngredientDTO ingredientDTO) {
        return ingredientService.create(ingredientDTO);
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public IngredientDTO updateStock(
            @PathVariable Long id,
            @Valid @RequestBody IngredientStockUpdateDTO updateRequest) {
        // Ép xuống implementation để gọi đúng method updateStock(...)
        return (ingredientService)
                .updateStock(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public void delete(@PathVariable Long id) {
        ingredientService.delete(id);
    }


    @PostMapping(
            value = "/import-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ImportResultDTO> importExcel(@RequestParam("file") MultipartFile file) {
        ImportResultDTO result = ingredientService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }


}

