package com.example.DtaAssigement.dto;


import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyRevenueDTO {
    private LocalDate date;
    private double amount;
}

