package com.example.DtaAssigement.controller;


import com.example.DtaAssigement.entity.Shift;
import com.example.DtaAssigement.repository.ShiftRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftRepository shiftRepository;

    // CREATE
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Shift> createShift(@Valid @RequestBody Shift shift) {
        return ResponseEntity.ok(shiftRepository.save(shift));
    }

    // READ ALL
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(shiftRepository.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<Shift> getShiftById(@PathVariable Long id) {
        return shiftRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Shift> updateShift(@PathVariable Long id, @Valid @RequestBody Shift updatedShift) {
        return shiftRepository.findById(id)
                .map(shift -> {
                    shift.setName(updatedShift.getName());
                    shift.setStartTime(updatedShift.getStartTime());
                    shift.setEndTime(updatedShift.getEndTime());
                    return ResponseEntity.ok(shiftRepository.save(shift));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        return shiftRepository.findById(id)
                .map(shift -> {
                    shiftRepository.delete(shift);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
