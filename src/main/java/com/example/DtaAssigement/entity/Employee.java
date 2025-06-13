package com.example.DtaAssigement.entity;

import com.example.DtaAssigement.ennum.EmploymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden=true)
    private Long id;

    @NotBlank @Size(max = 100)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Size(max = 15)
    private String phone;

    // Lương cơ bản (gross) theo tháng đối với full-time,
    // hoặc lương theo giờ đối với part-time
    @NotNull
    private BigDecimal baseSalary;
}
