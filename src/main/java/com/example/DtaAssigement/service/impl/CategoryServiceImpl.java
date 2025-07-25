package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.CategoryDTO;
import com.example.DtaAssigement.entity.Category;
import com.example.DtaAssigement.repository.CategoryRepository;
import com.example.DtaAssigement.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.example.DtaAssigement.mapper.CategoryMapper;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDTO> getAllCategories() {
        return repository.findAll()
                .stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public Optional<CategoryDTO> getCategoryById(Long id) {
        return repository.findById(id)
                .stream()
                .map(CategoryMapper::toDTO)
                .findFirst();
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)  // Evict cache for all categories when a category is created
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = CategoryMapper.toEntity(categoryDTO);
        Category savedCategory = repository.save(category);

        return CategoryMapper.toDTO(savedCategory);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "category", key = "#id")
    })
    public Category updateCategory(Long id, Category category) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "category", key = "#id")
    })
    public void deleteCategory(Long id) {
        repository.deleteById(id);
    }


}
