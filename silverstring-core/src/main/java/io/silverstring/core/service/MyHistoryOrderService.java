package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.repository.hibernate.MarketHistoryOrderRepository;
import io.silverstring.core.repository.hibernate.MyHistoryOrderRepository;
import io.silverstring.domain.dto.OrderDTO;
import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.hibernate.MyHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import io.silverstring.domain.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class MyHistoryOrderService {

    final MyHistoryOrderRepository myHistoryOrderRepository;

    @Autowired
    public MyHistoryOrderService(MyHistoryOrderRepository myHistoryOrderRepository) {
        this.myHistoryOrderRepository = myHistoryOrderRepository;
    }

    @HardTransational
    public MyHistoryOrder registMyHistory(MyHistoryOrder myHistoryOrder) {
        return myHistoryOrderRepository.save(myHistoryOrder);
    }

    public Page<MyHistoryOrder> getMyHistoryOrders(Long userId, OrderDTO.ReqMyHistoryOrders request) {
        PageRequest pageRequest = new PageRequest(request.getPageNo() == null ? 0 : Integer.valueOf(request.getPageNo()),request.getPageSize());
        Page<MyHistoryOrder> myHistoryOrdersPage = null;
        if (request.getFromCoin() == null) {
            myHistoryOrdersPage = myHistoryOrderRepository.findAllByUserIdOrderById(userId, pageRequest);
        } else {
            myHistoryOrdersPage = myHistoryOrderRepository.findAllByUserIdAndFromCoinOrderById(userId, request.getFromCoin(), pageRequest);
        }
        return myHistoryOrdersPage;
    }
}
