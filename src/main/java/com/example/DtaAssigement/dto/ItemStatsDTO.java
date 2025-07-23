package com.example.DtaAssigement.dto;


import java.math.BigDecimal;

public interface ItemStatsDTO {
    String getItemName();
    long getQuantitySold();
    BigDecimal getPrice();
    String getImageUrl();
}