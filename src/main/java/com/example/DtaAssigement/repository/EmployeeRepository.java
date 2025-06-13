package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

}
