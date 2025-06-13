package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Employee;
import com.example.DtaAssigement.entity.EmployeeShift;
import com.example.DtaAssigement.entity.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord,Long> {

    List<SalaryRecord> findByEmployeeIdAndMonth(Long employeeId, YearMonth month);
}
