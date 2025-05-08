package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.Category;
import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.repository.CategoryRepository;
import com.example.DtaAssigement.repository.MenuItemRepository;
import com.example.DtaAssigement.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository repository;
    private final CategoryRepository categoryrepository;

    @Override
    public List<MenuItem> getAllMenuItems() {
        return repository.findAll();
    }

    @Override
    public Optional<MenuItem> getMenuItemById(Long id) {
        return repository.findById(id);
    }

    @Override
    public MenuItem createMenuItem(MenuItem menuItem) {
        if (menuItem.getCategory() == null || menuItem.getCategory().getName() == null) {
            throw new IllegalArgumentException("Tên danh mục không hợp lệ (null hoặc không được cung cấp).");
        }

        String categoryName = menuItem.getCategory().getName();
        Category category = categoryrepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục với tên '" + categoryName + "' không tồn tại."));

        menuItem.setCategory(category);
        return repository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem menuItem) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(menuItem.getName());
                    existing.setPrice(menuItem.getPrice());
                    existing.setCategory(menuItem.getCategory());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id " + id));
    }

    @Override
    public void deleteMenuItem(Long id) {
        repository.deleteById(id);
    }
}

