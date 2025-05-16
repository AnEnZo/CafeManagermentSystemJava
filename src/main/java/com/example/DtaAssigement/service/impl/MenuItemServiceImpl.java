package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.Category;
import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.repository.CategoryRepository;
import com.example.DtaAssigement.repository.MenuItemRepository;
import com.example.DtaAssigement.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository repository;
    private final CategoryRepository categoryrepository;

    @Override
    @Cacheable(value = "menuItems", key = "'all'")
    public List<MenuItem> getAllMenuItems() {
        return repository.findAll();
    }

    @Override
    @Cacheable(value = "menuItem", key = "#id")
    public Optional<MenuItem> getMenuItemById(Long id) {
        return repository.findById(id);
    }

    @Override
    @CachePut(value = "menuItem", key = "#result.id") // cập nhật cache cho từng món
    @CacheEvict(value = "menuItems", allEntries = true) // xóa cache danh sách món ăn
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
    @CachePut(value = "menuItem", key = "#id")
    @CacheEvict(value = "menuItems", allEntries = true)
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
    @Caching(evict = {
            @CacheEvict(value = "menuItem", key = "#id"),
            @CacheEvict(value = "menuItems", allEntries = true)
    })
    public void deleteMenuItem(Long id) {
        repository.deleteById(id);
    }
}

