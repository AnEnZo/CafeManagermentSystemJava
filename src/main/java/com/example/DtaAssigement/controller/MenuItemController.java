package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.MenuItemDTO;
import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.entity.MenuItemIngredient;
import com.example.DtaAssigement.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@AllArgsConstructor
public class MenuItemController {

    private final MenuItemService service;
    private final MenuItemService menuItemService;


    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public List<MenuItem> getAll() {
        return service.getAllMenuItems();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public ResponseEntity<MenuItem> getById(@PathVariable Long id) {
        return service.getMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MenuItemDTO> create(@Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO created = service.createMenuItem(menuItemDTO);
        return ResponseEntity.created(URI.create("/api/menu-items/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MenuItemDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO updated = service.updateMenuItem(id, menuItemDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{menuItemId}/ingredients")
    public ResponseEntity<MenuItemIngredient> addIngredientToMenuItem(
            @PathVariable Long menuItemId,
            @RequestParam("ingredientId") Long ingredientId,
            @RequestParam("quantityUsed") double quantityUsed) {

        MenuItemIngredient added = menuItemService.addIngredient(menuItemId, ingredientId, quantityUsed);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(added);
    }

    @DeleteMapping("/{menuItemId}/ingredients/{menuItemIngredientId}")
    public ResponseEntity<Void> removeIngredientFromMenuItem(
            @PathVariable Long menuItemId,
            @PathVariable Long menuItemIngredientId) {

        menuItemService.removeIngredient(menuItemId, menuItemIngredientId);
        return ResponseEntity.noContent().build();
    }

}

