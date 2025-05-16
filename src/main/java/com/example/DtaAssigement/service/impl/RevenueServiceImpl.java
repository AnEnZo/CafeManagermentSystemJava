package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.repository.RevenueRepository;
import com.example.DtaAssigement.service.RevenueService;
import com.example.DtaAssigement.dto.RevenueSummary;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class RevenueServiceImpl implements RevenueService {

    private final RevenueRepository revenueRepo;

    @Override
    public void recordRevenue(LocalDate date, PaymentMethod method, double amount) {
        revenueRepo.recordRevenue(date, method.name(), amount);
    }

    @Override
    public double getRevenueByDate(LocalDate date) {
        return revenueRepo.getTotalByDate(date);
    }

    @Override
    public double getRevenueByMonth(int month, int year) {
        return revenueRepo.getTotalByMonth(month, year);
    }

    @Override
    public double getRevenueByPaymentMethod(PaymentMethod method, LocalDate start, LocalDate end) {
        return revenueRepo.getTotalByPaymentMethod(method.name(), start, end);
    }

    @Override
    public List<RevenueSummary> getRevenueGroupedByMethod(LocalDate start, LocalDate end) {
        return revenueRepo.getGroupedByMethod(start, end).stream()
                .map(arr -> new RevenueSummary(
                        PaymentMethod.valueOf((String) arr[0]),
                        ((Number) arr[1]).doubleValue()))
                .collect(Collectors.toList());
    }
}