package io.silverstring.core.service;

import io.silverstring.core.repository.hibernate.MarketHistoryOrderRepository;
import io.silverstring.domain.dto.GraphDTO;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class GraphService {

    private final MarketHistoryOrderRepository marketHistoryOrderRepository;

    @Autowired
    public GraphService(MarketHistoryOrderRepository marketHistoryOrderRepository) {
        this.marketHistoryOrderRepository = marketHistoryOrderRepository;
    }

    public GraphDTO.ResGraphData24H get24hGraphData(CoinEnum coinEnum) {
        List<MarketHistoryOrder> marketHistoryOrders = marketHistoryOrderRepository.getMarketHistoryOrderBetweenStartDtAndEndDtOrderDesc(coinEnum, LocalDateTime.now().minusHours(24), LocalDateTime.now());
        GraphDTO.ResGraphData24H response = new GraphDTO.ResGraphData24H();
        List<String> dates = new ArrayList<>();
        List<Long> prices = new ArrayList<>();

        Long avgPrice = 0l;
        Long sumPrice = 0l;
        Long minPrice = 0l;
        Long maxPrice = 0l;
        for (MarketHistoryOrder marketHistoryOrder : marketHistoryOrders) {
            dates.add(marketHistoryOrder.getCompletedDtm().toString());
            prices.add(marketHistoryOrder.getPrice().longValue());

            if (maxPrice <= marketHistoryOrder.getPrice().longValue()) {
                maxPrice = marketHistoryOrder.getPrice().longValue();
            }

            if (minPrice >= marketHistoryOrder.getPrice().longValue()) {
                minPrice = marketHistoryOrder.getPrice().longValue();
            }

            sumPrice += marketHistoryOrder.getPrice().longValue();
        }

        if (sumPrice > 0l) {
            avgPrice = sumPrice / marketHistoryOrders.size();
        }

        response.setDates(dates);
        response.setPrices(prices);
        response.setMinPrice(minPrice);
        response.setMaxPrice(maxPrice);
        response.setAvgPrice(avgPrice);
        return response;
    }
}
