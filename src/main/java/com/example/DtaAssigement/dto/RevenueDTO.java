package com.example.DtaAssigement.dto;

import java.time.LocalDate;

public interface RevenueDTO {
    LocalDate getPeriod();
    double    getTotal();
}