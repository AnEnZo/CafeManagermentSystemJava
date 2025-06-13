package com.example.DtaAssigement.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDTO {

    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Tên nguyên liệu không được để trống")
    private String name;

    @PositiveOrZero(message = "Số lượng tồn kho không được âm")
    private double quantityInStock;

    private String unit;

    @PositiveOrZero(message = "Giá nhập không được âm")
    private BigDecimal importPrice;

    @NotBlank(message = "Tên chi nhánh không được để trống")
    private String branchName;
}

