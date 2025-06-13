package com.example.DtaAssigement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.DtaAssigement.entity.Position;
import com.example.DtaAssigement.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/positions")
@Tag(name = "Position", description = "CRUD operations for Positions")
public class PositionController {

    @Autowired
    private PositionService service;

    @GetMapping
    @Operation(summary = "Get all positions")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<Position>> getAll() {
        return ResponseEntity.ok(service.getAllPositions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get position by ID")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<Position> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPositionById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new position")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Position> create(@Validated @RequestBody Position position) {
        Position created = service.createPosition(position);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing position")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Position> update(
            @PathVariable Long id,
            @Validated @RequestBody Position position) {
        return ResponseEntity.ok(service.updatePosition(id, position));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a position")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deletePosition(id);
        return ResponseEntity.noContent().build();
    }
}
