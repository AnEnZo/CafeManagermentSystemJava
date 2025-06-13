package com.example.DtaAssigement.mapper;


import com.example.DtaAssigement.dto.SalaryRecordDTO;
import com.example.DtaAssigement.entity.SalaryRecord;
import org.springframework.stereotype.Component;

@Component
public class SalaryRecordMapper {
    public SalaryRecordDTO toDto(SalaryRecord e) {
        if (e == null) return null;
        return SalaryRecordDTO.builder()
                .id(e.getId())
                .employeeId(e.getEmployee().getId())
                .month(e.getMonth())
                .grossSalary(e.getGrossSalary())
                .lateCount(e.getLateCount())
                .overtimeCount(e.getOvertimeCount())
                .daysOff(e.getDaysOff())
                .netSalary(e.getNetSalary())
                .build();
    }

    public SalaryRecord toEntity(SalaryRecordDTO dto) {
        if (dto == null) return null;
        SalaryRecord e = new SalaryRecord();
        e.setMonth(dto.getMonth());
        e.setGrossSalary(dto.getGrossSalary());
        e.setLateCount(dto.getLateCount());
        e.setOvertimeCount(dto.getOvertimeCount());
        e.setDaysOff(dto.getDaysOff());
        e.setNetSalary(dto.getNetSalary());
        return e;
    }


}

