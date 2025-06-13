package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.EmployeeShiftDTO;

import java.util.List;

public interface EmployeeShiftService {
    List<EmployeeShiftDTO> getAll();
    EmployeeShiftDTO getById(Long id);
    EmployeeShiftDTO create(EmployeeShiftDTO dto);
    EmployeeShiftDTO update(Long id, EmployeeShiftDTO dto);
    void delete(Long id);
}