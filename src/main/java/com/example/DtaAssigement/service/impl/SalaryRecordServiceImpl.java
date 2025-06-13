package com.example.DtaAssigement.service.impl;


import com.example.DtaAssigement.dto.SalaryRecordDTO;
import com.example.DtaAssigement.dto.SalaryRecordRequestDTO;
import com.example.DtaAssigement.ennum.EmploymentType;
import com.example.DtaAssigement.ennum.ShiftStatus;
import com.example.DtaAssigement.entity.EmployeeShift;
import com.example.DtaAssigement.entity.SalaryRecord;
import com.example.DtaAssigement.entity.Employee;
import com.example.DtaAssigement.mapper.SalaryRecordMapper;
import com.example.DtaAssigement.repository.EmployeeShiftRepository;
import com.example.DtaAssigement.repository.SalaryRecordRepository;
import com.example.DtaAssigement.repository.EmployeeRepository;
import com.example.DtaAssigement.service.SalaryRecordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.hpsf.Decimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class SalaryRecordServiceImpl implements SalaryRecordService {
    private final SalaryRecordRepository salaryRecordRepo;
    private final EmployeeRepository employeeRepo;
    private final SalaryRecordMapper salaryRecordMapper;
    private final EmployeeShiftRepository employeeShiftRepo;
    @Override
    public List<SalaryRecordDTO> getAll() {
        return salaryRecordRepo.findAll().stream()
                .map(salaryRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SalaryRecordDTO getById(Long id) {
        SalaryRecord e = salaryRecordRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SalaryRecord not found: " + id));
        return salaryRecordMapper.toDto(e);
    }

    @Override
    public SalaryRecordDTO create(SalaryRecordRequestDTO req) {
        Employee emp = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + req.getEmployeeId()));
        YearMonth month = req.getMonth();

        int totalDaysInMonth = month.lengthOfMonth();
        List<EmployeeShift> shifts = employeeShiftRepo.findByEmployeeAndWorkDateBetween(
                emp, month.atDay(1), month.atEndOfMonth());

        // Tính số ngày thực sự đi làm
        long workingDays = shifts.stream()
                .filter(s -> s.getStatus() == ShiftStatus.PRESENT
                        || s.getStatus() == ShiftStatus.LATE
                        || s.getStatus() == ShiftStatus.OVERTIME)
                .map(EmployeeShift::getWorkDate)
                .distinct()
                .count();


        int daysOff = totalDaysInMonth - (int) workingDays;
        if (daysOff < 0) daysOff = 0;

        // Tính số lần đi muộn và tăng ca
        long lateCount = shifts.stream()
                .filter(s -> s.getStatus() == ShiftStatus.LATE)
                .count();
        long overtimeCount = shifts.stream()
                .filter(s -> s.getStatus() == ShiftStatus.OVERTIME)
                .count();

        // Tính gross và net
        BigDecimal gross;
        BigDecimal net;
        if (emp.getEmploymentType() == EmploymentType.FULL_TIME) {
            gross = emp.getBaseSalary();
            net = gross.multiply(BigDecimal.valueOf(workingDays));
        } else {
            double hours = shifts.stream()
                    .filter(s -> s.getStatus() == ShiftStatus.PRESENT
                            || s.getStatus() == ShiftStatus.LATE
                            || s.getStatus() == ShiftStatus.OVERTIME)
                    .mapToDouble(s -> s.getShift().getDuration().toHours())
                    .sum();
            gross = emp.getBaseSalary().multiply(BigDecimal.valueOf(hours));
            net = gross;
        }

        // Điều chỉnh theo đi muộn và tăng ca
        net = net.subtract(BigDecimal.valueOf(lateCount*50000)).add(BigDecimal.valueOf(overtimeCount*50000));


        SalaryRecord rec = new SalaryRecord();
        rec.setEmployee(emp);
        rec.setMonth(month);
        rec.setGrossSalary(gross);
        rec.setLateCount(lateCount);
        rec.setOvertimeCount(overtimeCount);
        rec.setDaysOff((int) daysOff);
        rec.setNetSalary(net);
        SalaryRecord saved = salaryRecordRepo.save(rec);
        return salaryRecordMapper.toDto(saved);
    }


//    @Override
//    public SalaryRecordDTO update(Long id, SalaryRecordDTO dto) {
//        SalaryRecord e = salaryRecordRepo.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("SalaryRecord not found: " + id));
//
//        e.setMonth(dto.getMonth());
//        e.setGrossSalary(dto.getGrossSalary());
//        e.setDaysOff(dto.getDaysOff());
//        e.setNetSalary(dto.getNetSalary());
//
//        assignEmployee(dto, e);
//        SalaryRecord updated = salaryRecordRepo.save(e);
//        return salaryRecordMapper.toDto(updated);
//    }

    @Override
    public void delete(Long id) {
        salaryRecordRepo.deleteById(id);
    }

    private void assignEmployee(SalaryRecordDTO dto, SalaryRecord e) {
        Employee emp = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + dto.getEmployeeId()));
        e.setEmployee(emp);
    }

    public List<SalaryRecord> getByEmployeeAndMonth(Long employeeId, YearMonth month) {
        return salaryRecordRepo.findByEmployeeIdAndMonth(employeeId, month);
    }

}

