package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.DailyRevenueDTO;
import com.example.DtaAssigement.dto.RevenueSummary;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RevenueService {
    void recordRevenue(LocalDate date, PaymentMethod method, BigDecimal amount);
    double getRevenueByDate(LocalDate date);
    double getRevenueByMonth(int month, int year);
    double getRevenueByPaymentMethod(PaymentMethod method, LocalDate start, LocalDate end);
    List<RevenueSummary> getRevenueGroupedByMethod(LocalDate start, LocalDate end);
    List<DailyRevenueDTO> getDailyRevenueInMonth(int month, int year);
    List<Invoice> getInvoicesByDate(LocalDate date);
}
