package com.example.DtaAssigement.mapper;

import com.example.DtaAssigement.dto.EmployeeShiftDTO;
import com.example.DtaAssigement.entity.EmployeeShift;
import org.springframework.stereotype.Component;

@Component
public class EmployeeShiftMapper {

    public EmployeeShiftDTO toDto(EmployeeShift e) {
        if (e == null) return null;
        return EmployeeShiftDTO.builder()
                .id(e.getId())
                .employeeId(e.getEmployee().getId())
                .workDate(e.getWorkDate())
                .shiftId(e.getShift().getId())
                .status(e.getStatus())
                .build();
    }

    public EmployeeShift toEntity(EmployeeShiftDTO dto) {
        if (dto == null) return null;
        EmployeeShift e = new EmployeeShift();
        e.setWorkDate(dto.getWorkDate());
        e.setStatus(dto.getStatus());
        return e;
    }
}
