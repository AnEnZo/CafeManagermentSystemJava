package com.example.DtaAssigement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    @Schema(description = "Category ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotEmpty(message = "Tên danh mục không được để trống")
    @Size(max = 50, message = "Tên danh mục không được vượt quá 50 ký tự")
    private String name;

}
