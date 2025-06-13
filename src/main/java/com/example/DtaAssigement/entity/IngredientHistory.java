package com.example.DtaAssigement.entity;


import com.example.DtaAssigement.ennum.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ingredient_history")
public class IngredientHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Nguyên liệu không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @NotNull(message = "Loại giao dịch không được để trống")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Positive(message = "Số lượng phải lớn hơn 0")
    private double quantity;

    @NotNull
    private LocalDateTime transactionDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal priceAtTransaction; // nếu muốn lưu giá tại thời điểm đó

    @Column(nullable = false)
    private double quantityAfterTransaction;

    private String note; // ghi chú thêm nếu cần
}

