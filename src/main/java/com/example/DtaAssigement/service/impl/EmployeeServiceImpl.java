package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.EmployeeDTO;
import com.example.DtaAssigement.entity.Employee;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.Position;
import com.example.DtaAssigement.mapper.EmployeeMapper;
import com.example.DtaAssigement.repository.EmployeeRepository;
import com.example.DtaAssigement.repository.BranchRepository;
import com.example.DtaAssigement.repository.PositionRepository;
import com.example.DtaAssigement.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final BranchRepository branchRepo;
    private final PositionRepository posittionRepo;
    private final EmployeeMapper employeeMapper;



    @Override
    public List<EmployeeDTO> getAll() {
        return employeeRepo.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getById(Long id) {
        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        return employeeMapper.toDto(e);
    }

    @Override
    public EmployeeDTO create(EmployeeDTO dto) {
        Employee e = employeeMapper.toEntity(dto);
        assignBranchAndPosition(dto, e);
        Employee saved = employeeRepo.save(e);
        return employeeMapper.toDto(saved);
    }

    @Override
    public EmployeeDTO update(Long id, EmployeeDTO dto) {
        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));

        e.setFullName(dto.getFullName());
        e.setEmploymentType(dto.getEmploymentType());
        e.setPhone(dto.getPhone());
        e.setBaseSalary(dto.getBaseSalary());

        assignBranchAndPosition(dto, e);
        return employeeMapper.toDto(e);
    }

    @Override
    public void delete(Long id) {
        employeeRepo.deleteById(id);
    }

    private void assignBranchAndPosition(EmployeeDTO dto, Employee e) {
        Branch b = branchRepo.findByName(dto.getBranchName())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + dto.getBranchName()));
        Position p = posittionRepo.findByName(dto.getPositionName())
                .orElseThrow(() -> new EntityNotFoundException("Position not found: " + dto.getPositionName()));
        e.setBranch(b);
        e.setPosition(p);
    }
}
