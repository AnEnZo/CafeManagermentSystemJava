package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.MenuItemDTO;
import com.example.DtaAssigement.entity.*;
import com.example.DtaAssigement.mapper.MenuItemMapper;
import com.example.DtaAssigement.repository.*;
import com.example.DtaAssigement.service.MenuItemService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepo;
    private final CategoryRepository categoryrepository;
    private final CategoryRepository categoryRepo;


    @Override
    @Cacheable(value = "menuItems", key = "'all'")
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepo.findAll();
    }

    @Override
    @Cacheable(value = "menuItem", key = "#id")
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepo.findById(id);
    }

    @Override
    @Cacheable(value = "menuItemsByCategory", key = "#categoryName")
    public List<MenuItem> getMenuItemsByCategory(String categoryName) {
        // Có thể kiểm tra tồn tại Category nếu muốn:
        if (!categoryRepo.existsByName(categoryName)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category '" + categoryName + "' not found"
            );
        }
        return menuItemRepo.findByCategoryName(categoryName);
    }

    @Override
    @CachePut(value = "menuItem", key = "#result.id") // Cập nhật cache cho món ăn mới
    @CacheEvict(value = "menuItems", allEntries = true) // Xóa cache danh sách món ăn
    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        // Kiểm tra và lấy tên danh mục
        String categoryName = menuItemDTO.getCategory().getName();
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không hợp lệ (null hoặc rỗng).");
        }
        // Tìm danh mục theo tên và id
        Category category = categoryrepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục với tên '" + categoryName + "' không tồn tại."));
        // Chuyển DTO thành entity
        MenuItem menuItem = MenuItemMapper.toEntity(menuItemDTO, category);
        menuItem.setCategory(category); // Gán lại để đảm bảo đúng entity
        // Lưu món ăn vào database
        MenuItem created = menuItemRepo.save(menuItem);
        // Chuyển thành DTO để trả về
        return MenuItemMapper.toDTO(created);
    }


    @Override
    @CachePut(value = "menuItem", key = "#id")
    @CacheEvict(value = "menuItems", allEntries = true)
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        MenuItem existing = menuItemRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MenuItem not found with id " + id));

        if (menuItemDTO.getCategory() != null) {
            Category category = categoryRepo.findByName(menuItemDTO.getCategory().getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            existing.setCategory(category);
            existing.setName(menuItemDTO.getName());
            existing.setPrice(menuItemDTO.getPrice());
            existing.setImageUrl(menuItemDTO.getImageUrl());
        }else{
            throw new IllegalArgumentException("Category k dc để trống");
        }

        return MenuItemMapper.toDTO(menuItemRepo.save(existing));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "menuItem", key = "#id"),
            @CacheEvict(value = "menuItems", allEntries = true)
    })
    public void deleteMenuItem(Long id) {
        menuItemRepo.deleteById(id);
    }




}

