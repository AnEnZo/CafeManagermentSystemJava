package com.example.DtaAssigement.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRecordRequestDTO {
    @NotNull
    private Long employeeId;

    @NotNull
    private YearMonth month;
}
