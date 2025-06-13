package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.EmployeeShiftDTO;
import com.example.DtaAssigement.service.EmployeeShiftService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employeeShifts")
@AllArgsConstructor
public class EmployeeShiftController {
    private final EmployeeShiftService employeeShiftService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<EmployeeShiftDTO>> getAll() {
        return ResponseEntity.ok(employeeShiftService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<EmployeeShiftDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeShiftService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EmployeeShiftDTO> create(@Valid @RequestBody EmployeeShiftDTO dto) {
        EmployeeShiftDTO created = employeeShiftService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EmployeeShiftDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody EmployeeShiftDTO dto) {
        return ResponseEntity.ok(employeeShiftService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeShiftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
