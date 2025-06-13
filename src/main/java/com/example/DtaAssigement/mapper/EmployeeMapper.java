package com.example.DtaAssigement.mapper;

import com.example.DtaAssigement.dto.EmployeeDTO;
import com.example.DtaAssigement.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeDTO toDto(Employee e) {
        if (e == null) return null;
        return EmployeeDTO.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .branchName(e.getBranch().getName())
                .positionName(e.getPosition().getName())
                .employmentType(e.getEmploymentType())
                .phone(e.getPhone())
                .baseSalary(e.getBaseSalary())
                .build();
    }

    public Employee toEntity(EmployeeDTO dto) {
        if (dto == null) return null;
        Employee e = new Employee();
        e.setFullName(dto.getFullName());
        e.setEmploymentType(dto.getEmploymentType());
        e.setPhone(dto.getPhone());
        e.setBaseSalary(dto.getBaseSalary());
        return e;
    }
}