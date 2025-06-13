package com.example.DtaAssigement.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDTO {

    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 50, message = "Tên món không được vượt quá 50 ký tự")
    private String name;

    @Positive(message = "Giá món phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Danh mục món ăn không được để trống")
    private CategoryDTO category;

}
