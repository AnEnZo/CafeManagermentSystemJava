package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.MenuItem;
import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItem createMenuItem(MenuItem menuItem);
    MenuItem updateMenuItem(Long id, MenuItem menuItem);
    void deleteMenuItem(Long id);
}
