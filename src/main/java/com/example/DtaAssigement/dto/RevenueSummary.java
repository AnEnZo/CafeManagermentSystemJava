package com.example.DtaAssigement.dto;

import com.example.DtaAssigement.ennum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueSummary {
    private PaymentMethod paymentMethod;
    private double total;
}
