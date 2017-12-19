package io.silverstring.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaxMinMarketHistoryOrderDTO {
    private BigDecimal maxPrice;
    private BigDecimal minPrice;

    public MaxMinMarketHistoryOrderDTO(BigDecimal minPrice, BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }
}
