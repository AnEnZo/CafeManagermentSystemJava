package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.*;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.service.RevenueService;
import com.example.DtaAssigement.service.StatsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/revenues")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class RevenueController {
    private final StatsService statsService;
    private final RevenueService revenueService;


    @GetMapping("/items/top")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<ItemStatsDTO>> topItems(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(statsService.getTopSellingItems(topN));
    }

    @PostMapping("/record")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void record(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam PaymentMethod method,
            @RequestParam BigDecimal amount,
            @RequestParam Long branchId
    ) {
        revenueService.recordRevenue(date, method, amount,branchId);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public double getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long branchId) {
        return revenueService.getRevenueByDate(date,branchId);
    }

    @GetMapping("/month")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public double getByMonth(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam Long branchId) {
        return revenueService.getRevenueByMonth(month, year,branchId);
    }

    @GetMapping("/payment-method")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public double getByPaymentMethod(
            @RequestParam PaymentMethod method,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam Long branchId) {
        return revenueService.getRevenueByPaymentMethod(method, start, end,branchId);
    }

    @GetMapping("/grouped")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public List<RevenueSummary> getGrouped(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam Long branchId) {
        return revenueService.getRevenueGroupedByMethod(start, end,branchId);
    }

    @GetMapping("/invoicesByDate")
    public List<Invoice> getInvoicesByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return revenueService.getInvoicesByDate(date);
    }

}
