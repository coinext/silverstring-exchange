package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.repository.hibernate.MarketHistoryOrderRepository;
import io.silverstring.domain.dto.OrderDTO;
import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import io.silverstring.domain.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MarketHistoryOrderService {

    final MarketHistoryOrderRepository marketHistoryOrderRepository;

    @Autowired
    public MarketHistoryOrderService(MarketHistoryOrderRepository marketHistoryOrderRepository) {
        this.marketHistoryOrderRepository = marketHistoryOrderRepository;
    }

    public List<MarketHistoryOrder> getMarketHistoryOrders(OrderDTO.ReqMarketHistoryOrders request) {
        PageRequest pageRequest = new PageRequest(request.getPageNo() == null ? 0 : Integer.valueOf(request.getPageNo()),request.getPageSize());
        List<MarketHistoryOrder> historyOrders = marketHistoryOrderRepository.findAllByFromCoinOrderById(request.getFromCoin(), pageRequest).getContent();
        return historyOrders;
    }

    @HardTransational
    public MarketHistoryOrder registMarketHistory(Order targetOrder
            , BigDecimal targetAmount
            , BigDecimal targetPrice
            , Order candidateOrder
            , BigDecimal candidateAmount
            , BigDecimal candidatePrice) {

        MarketHistoryOrder marketHistoryOrder = new MarketHistoryOrder();
        if (targetOrder.getId() > candidateOrder.getId()) {
            marketHistoryOrder.setUserId(targetOrder.getUserId());
            marketHistoryOrder.setOrderId(targetOrder.getId());
            marketHistoryOrder.setAmount(targetAmount);
            marketHistoryOrder.setOrderType(targetOrder.getOrderType());
            marketHistoryOrder.setPrice(targetPrice);
            marketHistoryOrder.setToCoin(targetOrder.getToCoin());
            marketHistoryOrder.setFromCoin(targetOrder.getFromCoin());
        } else {
            marketHistoryOrder.setUserId(candidateOrder.getUserId());
            marketHistoryOrder.setOrderId(candidateOrder.getId());
            marketHistoryOrder.setAmount(candidateAmount);
            marketHistoryOrder.setOrderType(candidateOrder.getOrderType());
            marketHistoryOrder.setPrice(candidatePrice);
            marketHistoryOrder.setToCoin(candidateOrder.getToCoin());
            marketHistoryOrder.setFromCoin(candidateOrder.getFromCoin());
        }
        marketHistoryOrder.setCompletedDtm(LocalDateTime.now());
        marketHistoryOrder.setDt(DateUtil.FORMATTER_YYYY_MM_DD_HH_MM.format(LocalDateTime.now()));
        marketHistoryOrder.setRegDtm(LocalDateTime.now());
        marketHistoryOrder.setStatus(OrderStatus.COMPLETED);

        return marketHistoryOrderRepository.save(marketHistoryOrder);
    }
}
