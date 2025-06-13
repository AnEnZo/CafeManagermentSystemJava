package com.example.DtaAssigement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    @Schema(hidden = true)
    private Long id;

    @NotEmpty(message = "Tên danh mục không được để trống")
    @Size(max = 50, message = "Tên danh mục không được vượt quá 50 ký tự")
    private String name;

    @NotEmpty(message = "Tên cơ sở không được để trống")
    private String branchName;
}
