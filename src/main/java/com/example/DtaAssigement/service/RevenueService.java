package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.RevenueSummary;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RevenueService {
    void recordRevenue(LocalDate date, PaymentMethod method, BigDecimal amount,Long branchId);
    double getRevenueByDate(LocalDate date, Long branchId);
    double getRevenueByMonth(int month, int year, Long branchId);
    double getRevenueByPaymentMethod(PaymentMethod method, LocalDate start, LocalDate end, Long branchId);
    List<RevenueSummary> getRevenueGroupedByMethod(LocalDate start, LocalDate end, Long branchId);

    List<Invoice> getInvoicesByDate(LocalDate date);
}
