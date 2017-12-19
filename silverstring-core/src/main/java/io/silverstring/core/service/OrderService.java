package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.repository.hibernate.MarketHistoryOrderRepository;
import io.silverstring.core.repository.hibernate.OrderRepository;
import io.silverstring.domain.dto.GroupOrderDTO;
import io.silverstring.domain.dto.OrderDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.enums.OrderType;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;


    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Page<Order> getMyOrders(Long userId, OrderDTO.ReqMyOrders request) {
        PageRequest pageRequest = new PageRequest(request.getPageNo() == null ? 0 : Integer.valueOf(request.getPageNo()),request.getPageSize());
        Page<Order> orders = orderRepository.findAllByUserIdAndCoinAndStatus(userId, new Coin(request.getFromCoin()), OrderStatus.PLACED, pageRequest);
        return orders;
    }

    public OrderDTO.ResHogaOrders getHogaOrders(OrderDTO.ReqHogaOrders request) {

        OrderDTO.ResHogaOrders response = new OrderDTO.ResHogaOrders();

        List<GroupOrderDTO> buys = orderRepository.getHogaOrders(
                OrderStatus.PLACED
                , OrderType.BUY
                , new Coin(request.getToCoin())
                , new Coin(request.getFromCoin())
                , new PageRequest(request.getBuyPageNo() == null ? 0 : Integer.valueOf(request.getBuyPageNo()),
                        Integer.valueOf(request.getBuyPageSize())
                )).getContent();
        response.setBuy(buys);

        List<GroupOrderDTO> sells = orderRepository.getHogaOrders(
                OrderStatus.PLACED
                , OrderType.SELL
                , new Coin(request.getFromCoin())
                , new Coin(request.getToCoin())
                , new PageRequest(request.getSellPageNo() == null ? 0 : Integer.valueOf(request.getSellPageNo()),
                        Integer.valueOf(request.getSellPageSize())
                )).getContent();
        response.setSell(sells);
        return response;
    }

    @HardTransational
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order get(Long userId, Long orderId) {
        Optional<Order> order = orderRepository.findOneByIdAndUserId(orderId, userId);
        if (!order.isPresent()) {
            throw new ExchangeException(CodeEnum.ORDER_NOT_EXIST);
        }
        return order.get();
    }

    public List<Order> getTradeCandidateOrders(Order order) {
        List<Order> candidateOrders = null;

        if (OrderType.BUY.equals(order.getOrderType())) {
            Optional<Page<Order>> orders = orderRepository.getTradeCandidateOrdersSell(
                    order.getToCoin()
                    , order.getFromCoin()
                    , OrderStatus.PLACED
                    , OrderType.SELL
                    , order.getPrice()
                    , LocalDateTime.now().minusDays(365l)
                    , new PageRequest(0, 100)
            );

            if (orders.isPresent()) {
                candidateOrders = orders.get().getContent();
            }

        } else if (OrderType.SELL.equals(order.getOrderType())) {

            Optional<Page<Order>> orders = orderRepository.getTradeCandidateOrdersBuy(
                    order.getToCoin()
                    , order.getFromCoin()
                    , OrderStatus.PLACED
                    , OrderType.BUY
                    , order.getPrice()
                    , LocalDateTime.now().minusDays(365l)
                    , new PageRequest(0, 100)
            );

            if (orders.isPresent()) {
                candidateOrders = orders.get().getContent();
            }
        } else {
            throw new ExchangeException(CodeEnum.INVALID_ORDER_TYPE);
        }
        return candidateOrders;
    }

    @HardTransational
    public Order buy(Long userId, BigDecimal price, BigDecimal amount, BigDecimal fee, Coin fromCoin, Coin toCoin) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setAmountRemaining(amount);
        order.setCompletedDtm(null);
        order.setOrderType(OrderType.BUY);
        order.setPrice(price);
        order.setRegDtm(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setFromCoin(fromCoin);
        order.setToCoin(toCoin);
        order.setCancelDtm(null);
        return orderRepository.save(order);
    }

    @HardTransational
    public Order sell(Long userId, BigDecimal price, BigDecimal amount, BigDecimal fee, Coin fromCoin, Coin toCoin) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setAmountRemaining(amount);
        order.setCompletedDtm(null);
        order.setOrderType(OrderType.SELL);
        order.setPrice(price);
        order.setRegDtm(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setFromCoin(fromCoin);
        order.setToCoin(toCoin);
        order.setCancelDtm(null);
        return orderRepository.save(order);
    }
}
