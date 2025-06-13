package com.example.DtaAssigement.service.impl;


import com.example.DtaAssigement.dto.EmployeeShiftDTO;
import com.example.DtaAssigement.entity.EmployeeShift;
import com.example.DtaAssigement.entity.Employee;
import com.example.DtaAssigement.entity.Shift;
import com.example.DtaAssigement.mapper.EmployeeShiftMapper;
import com.example.DtaAssigement.repository.EmployeeShiftRepository;
import com.example.DtaAssigement.repository.EmployeeRepository;
import com.example.DtaAssigement.repository.ShiftRepository;
import com.example.DtaAssigement.service.EmployeeShiftService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class EmployeeShiftServiceImpl implements EmployeeShiftService {
    private final EmployeeShiftRepository employeeShiftRepo;
    private final EmployeeRepository employeeRepo;
    private final ShiftRepository shiftRepo;
    private final EmployeeShiftMapper employeeShiftMapper;


    @Override
    public List<EmployeeShiftDTO> getAll() {
        return employeeShiftRepo.findAll().stream()
                .map(employeeShiftMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeShiftDTO getById(Long id) {
        EmployeeShift e = employeeShiftRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EmployeeShift not found: " + id));
        return employeeShiftMapper.toDto(e);
    }

    @Override
    public EmployeeShiftDTO create(EmployeeShiftDTO dto) {
        EmployeeShift e = employeeShiftMapper.toEntity(dto);
        assignRelations(dto, e);
        EmployeeShift saved = employeeShiftRepo.save(e);
        return employeeShiftMapper.toDto(saved);
    }

    @Override
    public EmployeeShiftDTO update(Long id, EmployeeShiftDTO dto) {
        EmployeeShift e = employeeShiftRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EmployeeShift not found: " + id));

        e.setWorkDate(dto.getWorkDate());
        e.setStatus(dto.getStatus());
        assignRelations(dto, e);
        EmployeeShift updated = employeeShiftRepo.save(e);
        return employeeShiftMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        employeeShiftRepo.deleteById(id);
    }

    private void assignRelations(EmployeeShiftDTO dto, EmployeeShift e) {
        Employee emp = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + dto.getEmployeeId()));
        Shift s = shiftRepo.findById(dto.getShiftId())
                .orElseThrow(() -> new EntityNotFoundException("Shift not found: " + dto.getShiftId()));
        e.setEmployee(emp);
        e.setShift(s);
    }
}

