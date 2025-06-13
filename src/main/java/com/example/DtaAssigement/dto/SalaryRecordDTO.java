package com.example.DtaAssigement.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRecordDTO {
    @Schema(hidden=true)
    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private YearMonth month;

    @NotNull
    private BigDecimal grossSalary;

    @NotNull
    private long lateCount;
    @NotNull
    private long overtimeCount;

    @NotNull
    private Integer daysOff;

    @NotNull
    private BigDecimal netSalary;
}