package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.ItemStatsDTO;

import java.util.List;

public interface StatsService {

    List<ItemStatsDTO> getTopSellingItems(int topN);
}
