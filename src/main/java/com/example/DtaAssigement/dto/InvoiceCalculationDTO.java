package com.example.DtaAssigement.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceCalculationDTO {
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
}
