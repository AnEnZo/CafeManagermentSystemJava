package com.example.DtaAssigement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportErrorDTO {
    private int rowNumber;
    private String name;
    private String quantityChange;
    private String importPrice;
    private String errorCode;      // ví dụ: "MISSING_NAME", "INVALID_NUMBER", ...
    private String errorMessage;   // ví dụ: "Thiếu trường 'name' (ô trống)."
}

