package com.example.DtaAssigement.dto;

import com.example.DtaAssigement.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {
    @NotNull(message = "Table ID is required", groups = OnCreate.class)
    private Long tableId;
}
