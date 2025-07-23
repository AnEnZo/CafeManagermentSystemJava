package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.MenuItemDTO;
import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.mapper.MenuItemMapper;
import com.example.DtaAssigement.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu-items")
@AllArgsConstructor
public class MenuItemController {

    private final MenuItemService service;
    private final MenuItemService menuItemService;


    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public ResponseEntity<List<MenuItemDTO>> getAll() {
        List<MenuItemDTO> dtos = service.getAllMenuItems().stream()
                .map(MenuItemMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public ResponseEntity<MenuItemDTO> getById(@PathVariable Long id) {
        return service.getMenuItemById(id)
                .map(MenuItemMapper::toDTO)
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

    @GetMapping("/category/{name}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public ResponseEntity<List<MenuItemDTO>> getByCategoryName(@PathVariable("name") String name) {
        List<MenuItemDTO> dtos = service.getMenuItemsByCategory(name).stream()
                .map(MenuItemMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


}

