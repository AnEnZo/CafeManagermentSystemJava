package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.RestaurantTableDTO;
import com.example.DtaAssigement.entity.RestaurantTable;
import com.example.DtaAssigement.service.TableService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/tables")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TableController {

    private final TableService tableService;

    // Xem danh sách bàn
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<RestaurantTableDTO>> getAllTables() {
        List<RestaurantTableDTO> tables = tableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    // Tạo bàn mới
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RestaurantTableDTO> createTable( @Valid @RequestBody RestaurantTableDTO tableDTO) {
        RestaurantTableDTO created = tableService.createTable(tableDTO);
        return ResponseEntity.ok(created);
    }

    // Cập nhật trạng thái bàn
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RestaurantTable> updateStatus(@PathVariable Long id, @RequestParam boolean available) {
        RestaurantTable updated = tableService.updateTableStatus(id, available);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<RestaurantTableDTO> deleteTable(@PathVariable Long id){
        boolean deleted = tableService.deleteTable(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Map<String, Long>> getTableStatistics(@RequestParam String branchName) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalTables", tableService.getTotalTables(branchName));
        stats.put("availableTables", tableService.getAvailableTables(branchName));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<RestaurantTableDTO>> getAvailableTables(@RequestParam String branchName) {
        return ResponseEntity.ok(tableService.getListAvailableTables(branchName));
    }

}
