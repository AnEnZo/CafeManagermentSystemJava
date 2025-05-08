package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.entity.MenuItem;
import com.example.DtaAssigement.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService service;

    @Autowired
    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

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
    public ResponseEntity<MenuItem> create( @Valid @RequestBody MenuItem menuItem) {
        MenuItem created = service.createMenuItem(menuItem);
        return ResponseEntity.created(URI.create("/api/menu-items/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MenuItem> update(
            @PathVariable Long id,
            @Valid @RequestBody MenuItem menuItem) {
        MenuItem updated = service.updateMenuItem(id, menuItem);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}

