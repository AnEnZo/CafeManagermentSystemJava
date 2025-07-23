package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.DailyRevenueDTO;
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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class RevenueServiceImpl implements RevenueService {

    private final RevenueRepository revenueRepo;

    @Override
    public void recordRevenue(LocalDate date, PaymentMethod method, BigDecimal amount) {
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

    @Override
    public List<Invoice> getInvoicesByDate(LocalDate date) {
        return revenueRepo.findByDate(date)
                .map(Revenue::getInvoices)
                .orElseGet(Collections::emptyList);
    }


    @Override
    public List<DailyRevenueDTO> getDailyRevenueInMonth(int month, int year) {
        List<DailyRevenueDTO> result = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            Double amount = revenueRepo.getTotalByDate(date);
            result.add(new DailyRevenueDTO(date, amount != null ? amount : 0.0));
        }
        return result;
    }



}