package com.example.DtaAssigement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RestaurantTableDTO {

    @NotBlank(message = "Tên bàn không được để trống")
    private String name;

    @NotNull(message = "Trạng thái bàn (available) là bắt buộc")
    private Boolean available;

    @NotNull(message = "Sức chứa là bắt buộc")
    @Min(value = 1, message = "Sức chứa phải lớn hơn hoặc bằng 1")
    private Integer capacity;

    @NotBlank(message = "Tên chi nhánh không được để trống")
    private String branchName;

}
