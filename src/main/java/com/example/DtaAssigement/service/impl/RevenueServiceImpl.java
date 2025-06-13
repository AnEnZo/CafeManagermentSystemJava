package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.Revenue;
import com.example.DtaAssigement.repository.RevenueRepository;
import com.example.DtaAssigement.service.RevenueService;
import com.example.DtaAssigement.dto.RevenueSummary;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class RevenueServiceImpl implements RevenueService {

    private final RevenueRepository revenueRepo;

    @Override
    public void recordRevenue(LocalDate date, PaymentMethod method, BigDecimal amount,Long branchId) {
        revenueRepo.recordRevenueAndBranch(date, method.name(), amount,branchId);
    }

    @Override
    public double getRevenueByDate(LocalDate date, Long branchId) {
        return revenueRepo.getTotalByDateAndBranch(date,branchId);
    }

    @Override
    public double getRevenueByMonth(int month, int year, Long branchId) {
        return revenueRepo.getTotalByMonthAndBranch(month, year,branchId);
    }

    @Override
    public double getRevenueByPaymentMethod(PaymentMethod method, LocalDate start, LocalDate end, Long branchId) {
        return revenueRepo.getTotalByPaymentMethodAndBranch(method.name(), start, end,branchId);
    }

    @Override
    public List<RevenueSummary> getRevenueGroupedByMethod(LocalDate start, LocalDate end, Long branchId) {
        return revenueRepo.getGroupedByMethodAndBranch(start, end,branchId).stream()
                .map(arr -> new RevenueSummary(
                        PaymentMethod.valueOf((String) arr[0]),
                        ((Number) arr[1]).doubleValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> getInvoicesByDate(LocalDate date) {
        Revenue revenue = revenueRepo.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Revenue not found with date: " + date));
        return revenue.getInvoices();
    }
}