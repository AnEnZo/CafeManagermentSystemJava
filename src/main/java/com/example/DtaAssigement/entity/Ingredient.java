package com.example.DtaAssigement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên nguyên liệu không được để trống")
    private String name;

    @PositiveOrZero(message = "Số lượng tồn kho không được âm")
    private double quantityInStock; // ví dụ: 100 (gam, ml, đơn vị,...)

    @NotBlank(message = "đơm vị không được để trống")
    private String unit; // gam, ml, quả, chai...

    @PositiveOrZero(message = "Giá nhập không được âm")
    @Column(precision = 12, scale = 2)
    private BigDecimal importPrice;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(
            mappedBy = "ingredient",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Schema(hidden=true)
    private List<IngredientHistory> histories = new ArrayList<>();
}


