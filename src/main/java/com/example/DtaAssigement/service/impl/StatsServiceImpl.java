package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.ItemStatsDTO;
import com.example.DtaAssigement.repository.InvoiceRepository;
import com.example.DtaAssigement.repository.OrderItemRepository;
import com.example.DtaAssigement.repository.OrderRepository;
import com.example.DtaAssigement.service.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final OrderItemRepository orderItemRepo;

    @Override
    public List<ItemStatsDTO> getTopSellingItems(int topN) {
        Page<ItemStatsDTO> topItemsPage = orderItemRepo.findTopSellingItems(PageRequest.of(0, topN));
        return topItemsPage.getContent();  // Lấy danh sách ItemStatsDTO từ Page
    }


}
