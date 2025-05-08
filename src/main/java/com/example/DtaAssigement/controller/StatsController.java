package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.*;
import com.example.DtaAssigement.service.StatsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }


    @GetMapping("/revenue/daily")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<RevenueDTO>> dailyRevenue() {
        return ResponseEntity.ok(statsService.getDailyRevenue());
    }


    @GetMapping("/revenue/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<RevenueDTO>> monthlyRevenue() {
        return ResponseEntity.ok(statsService.getMonthlyRevenue());
    }


    @GetMapping("/items/top")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<List<ItemStatsDTO>> topItems(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(statsService.getTopSellingItems(topN));
    }


}
