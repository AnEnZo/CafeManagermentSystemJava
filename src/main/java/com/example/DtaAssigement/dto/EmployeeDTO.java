package com.example.DtaAssigement.dto;

import com.example.DtaAssigement.ennum.EmploymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Employee entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    @Schema(hidden=true)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Size(max = 100)
    private String branchName;

    @NotBlank
    @Size(max = 50)
    private String positionName;

    @NotNull
    private EmploymentType employmentType;

    @Size(max = 15)
    private String phone;

    @NotNull
    private BigDecimal baseSalary;
}
