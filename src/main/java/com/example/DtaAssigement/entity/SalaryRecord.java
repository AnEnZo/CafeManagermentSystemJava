package com.example.DtaAssigement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden=true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @NotNull
    private YearMonth month; // Ví dụ: 2025-05

    @NotNull
    private BigDecimal grossSalary; // baseSalary hoặc baseSalary * số giờ làm cho part-time

    @NotNull
    private long lateCount;

    @NotNull
    private long overtimeCount;

    @NotNull
    private Integer daysOff;   // tổng ngày nghỉ trong tháng

    // nhân viên full time netSalary = grossSalary × (ngày làm / tổng ngày làm việc trong tháng)
    // nhân viên parttime netSalary = baseSalary × tổng giờ làm thật tế

    @NotNull
    private BigDecimal netSalary;  // grossSalary trừ phần lương ngày nghỉ

}
