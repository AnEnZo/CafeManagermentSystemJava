package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.MenuItemDTO;
import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.entity.MenuItemIngredient;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO);
    MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO);
    void deleteMenuItem(Long id);
    MenuItemIngredient addIngredient(Long menuItemId, Long ingredientId, double quantityUsed);
    void removeIngredient(Long menuItemId, Long menuItemIngredientId);
}
