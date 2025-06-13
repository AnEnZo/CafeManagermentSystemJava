package com.example.DtaAssigement.dto;

import com.example.DtaAssigement.ennum.ShiftStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Data Transfer Object for EmployeeShift entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeShiftDTO {
    @Schema(hidden=true)
    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate workDate;

    @NotNull
    private Long shiftId;

    @NotNull
    private ShiftStatus status;
}
