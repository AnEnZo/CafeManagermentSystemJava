package com.example.DtaAssigement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientStockUpdateDTO {


    @PositiveOrZero(message = "Giá nhập không được âm")
    private BigDecimal importPrice;

    /**
     * Số lượng thay đổi:
     *  - Nếu > 0: sẽ được coi là nhập kho (IMPORT)
     *  - Nếu < 0: sẽ được coi là xuất kho (EXPORT)
     */
    @NotNull(message = "Số lượng nhập/xuất không được để trống")
    private Double quantityChange;
}
