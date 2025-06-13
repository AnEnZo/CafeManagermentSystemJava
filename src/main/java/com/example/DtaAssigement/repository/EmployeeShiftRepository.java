package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Employee;
import com.example.DtaAssigement.entity.EmployeeShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift,Long>{
    List<EmployeeShift> findByEmployeeAndWorkDateBetween(Employee employee,
                                                         LocalDate start,
                                                         LocalDate end);
}
