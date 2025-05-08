package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.Category;
import com.example.DtaAssigement.repository.CategoryRepository;
import com.example.DtaAssigement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Category createCategory(Category category) {
        return repository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    @Override
    public void deleteCategory(Long id) {
        repository.deleteById(id);
    }
}
