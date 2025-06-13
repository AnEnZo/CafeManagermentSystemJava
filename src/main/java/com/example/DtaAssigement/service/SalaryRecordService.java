package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.SalaryRecordDTO;
import com.example.DtaAssigement.dto.SalaryRecordRequestDTO;
import com.example.DtaAssigement.entity.SalaryRecord;

import java.time.YearMonth;
import java.util.List;

public interface SalaryRecordService {
    List<SalaryRecordDTO> getAll();
    SalaryRecordDTO getById(Long id);
    SalaryRecordDTO create(SalaryRecordRequestDTO request);
//    SalaryRecordDTO update(Long id, SalaryRecordDTO dto);
    void delete(Long id);
    List<SalaryRecord> getByEmployeeAndMonth(Long employeeId, YearMonth month);
}