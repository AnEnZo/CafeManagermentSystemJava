package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.RevenueSummary;
import com.example.DtaAssigement.ennum.PaymentMethod;

import java.time.LocalDate;
import java.util.List;

public interface RevenueService {
    void recordRevenue(LocalDate date, PaymentMethod method, double amount);
    double getRevenueByDate(LocalDate date);
    double getRevenueByMonth(int month, int year);
    double getRevenueByPaymentMethod(PaymentMethod method, LocalDate start, LocalDate end);
    List<RevenueSummary> getRevenueGroupedByMethod(LocalDate start, LocalDate end);
}
