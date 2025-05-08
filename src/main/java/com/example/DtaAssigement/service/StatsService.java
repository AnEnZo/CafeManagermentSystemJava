package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.ItemStatsDTO;
import com.example.DtaAssigement.dto.RevenueDTO;

import java.util.List;

public interface StatsService {

    List<RevenueDTO> getDailyRevenue();
    List<RevenueDTO> getMonthlyRevenue();
    List<ItemStatsDTO> getTopSellingItems(int topN);
}
