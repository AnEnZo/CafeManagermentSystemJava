package com.example.DtaAssigement.service;


import com.example.DtaAssigement.dto.CategoryDTO;
import com.example.DtaAssigement.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    Optional<CategoryDTO> getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);

}

