package com.example.DtaAssigement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RestaurantTableDTO {
    @Schema(description = "Category ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Tên bàn không được để trống")
    private String name;

    @NotNull(message = "Trạng thái bàn (available) là bắt buộc")
    private Boolean available;

    @NotNull(message = "Sức chứa là bắt buộc")
    @Min(value = 1, message = "Sức chứa phải lớn hơn hoặc bằng 1")
    private Integer capacity;


}
