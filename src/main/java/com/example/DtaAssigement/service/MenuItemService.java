package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.MenuItemDTO;
import com.example.DtaAssigement.entity.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO);
    MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO);
    void deleteMenuItem(Long id);
    List<MenuItem> getMenuItemsByCategory(String categoryName);
}
