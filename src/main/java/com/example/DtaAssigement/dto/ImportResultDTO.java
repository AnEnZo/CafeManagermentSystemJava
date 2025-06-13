package com.example.DtaAssigement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResultDTO {
    private int successCount;
    private int failureCount;
    private List<IngredientDTO> successList;
    private List<ImportErrorDTO> errorList;
}