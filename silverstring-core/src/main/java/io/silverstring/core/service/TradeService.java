package io.silverstring.core.service;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.util.WalletUtil;
import io.silverstring.domain.dto.MessagePacket;
import io.silverstring.domain.dto.TradeStatusDTO;
import io.silverstring.domain.enums.*;
import io.silverstring.domain.hibernate.MyHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import io.silverstring.domain.util.CompareUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
public class TradeService {

    private final OrderService orderService;
    private final WalletService walletService;
    private final MyHistoryOrderService myHistoryOrderService;
    private final MarketHistoryOrderService marketHistoryOrderService;
    private final MqPublisher mqPublisher;
    private final CoinService coinService;
    private final EntityManager entityManager;

    @Autowired
    public TradeService(OrderService orderService, WalletService walletService, MyHistoryOrderService myHistoryOrderService, MarketHistoryOrderService marketHistoryOrderService, MqPublisher mqPublisher, CoinService coinService, EntityManager entityManager) {
        this.orderService = orderService;
        this.walletService = walletService;
        this.myHistoryOrderService = myHistoryOrderService;
        this.marketHistoryOrderService = marketHistoryOrderService;
        this.mqPublisher = mqPublisher;
        this.coinService = coinService;
        this.entityManager = entityManager;
    }

    @Builder
    private static class CalculatedOrdetStatusAndAmount {
        private OrderStatus targetOrderStatus;
        private BigDecimal targetReminingOrderAmount;
        private BigDecimal targetAmount;
        private BigDecimal targetPrice;

        private OrderStatus candidateOrderStatus;
        private BigDecimal candidateReminingOrderAmount;
        private BigDecimal candidateAmount;
        private BigDecimal candidatePrice;
    }

    private CalculatedOrdetStatusAndAmount getCalculatedOrdetStatusAndAmount(Order targetOrder, Order candidateOrder) {
        final BigDecimal _compareAmount = targetOrder.getAmountRemaining().subtract(candidateOrder.getAmountRemaining());
        //(targetOrder == candidateOrder)
        if (CompareUtil.Condition.EQ.equals(CompareUtil.compareToZero(_compareAmount))) {
            return CalculatedOrdetStatusAndAmount.builder()
                    .targetOrderStatus(OrderStatus.COMPLETED)
                    .candidateOrderStatus(OrderStatus.COMPLETED)
                    .targetReminingOrderAmount(BigDecimal.ZERO)
                    .candidateReminingOrderAmount(BigDecimal.ZERO)
                    .targetAmount(targetOrder.getAmountRemaining())
                    .targetPrice(candidateOrder.getPrice())
                    .candidateAmount(candidateOrder.getAmountRemaining())
                    .candidatePrice(candidateOrder.getPrice())
                    .build();
        }
        //(targetOrder > candidateOrder)
        else if (CompareUtil.Condition.GT.equals(CompareUtil.compareToZero(_compareAmount))) {
            return CalculatedOrdetStatusAndAmount.builder()
                    .targetOrderStatus(OrderStatus.PLACED)
                    .candidateOrderStatus(OrderStatus.COMPLETED)
                    .targetReminingOrderAmount(targetOrder.getAmountRemaining().subtract(candidateOrder.getAmountRemaining()))
                    .candidateReminingOrderAmount(BigDecimal.ZERO)
                    .targetAmount(candidateOrder.getAmountRemaining())
                    .targetPrice(candidateOrder.getPrice())
                    .candidateAmount(candidateOrder.getAmountRemaining())
                    .candidatePrice(candidateOrder.getPrice())
                    .build();
        }
        //(targetOrder < candidateOrder)
        else if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(_compareAmount))) {
            return CalculatedOrdetStatusAndAmount.builder()
                    .targetOrderStatus(OrderStatus.COMPLETED)
                    .candidateOrderStatus(OrderStatus.PLACED)
                    .targetReminingOrderAmount(BigDecimal.ZERO)
                    .candidateReminingOrderAmount(candidateOrder.getAmountRemaining().subtract(targetOrder.getAmountRemaining()))
                    .targetAmount(targetOrder.getAmountRemaining())
                    .targetPrice(candidateOrder.getPrice())
                    .candidateAmount(targetOrder.getAmountRemaining())
                    .candidatePrice(candidateOrder.getPrice())
                    .build();
        } else {
            return null;
        }
    }

    private boolean isAvailableTradeOrderStatus(OrderStatus targetOrderStatus, OrderStatus candidateOrderStatus) {
        if (OrderStatus.PLACED.equals(targetOrderStatus) && OrderStatus.PLACED.equals(candidateOrderStatus)) {
            return true;
        }
        return false;
    }


    @HardTransational
    public MyHistoryOrder tradePartial(Order order, Order _candidateOrder) {
        Order targetOrder = orderService.get(order.getUserId(), order.getId());
        if (!OrderStatus.PLACED.equals(targetOrder.getStatus())) {
            return null;
        }

        Order candidateOrder = orderService.get(_candidateOrder.getUserId(), _candidateOrder.getId());
        if (!isAvailableTradeOrderStatus(targetOrder.getStatus(), candidateOrder.getStatus())) {
            return null;
        }

        CalculatedOrdetStatusAndAmount calculatedOrdetStatusAndAmount = getCalculatedOrdetStatusAndAmount(targetOrder, candidateOrder);
        if (calculatedOrdetStatusAndAmount == null) {
            return null;
        }

        //trade status update
        LocalDateTime now = LocalDateTime.now();
        targetOrder.setAmountRemaining(calculatedOrdetStatusAndAmount.targetReminingOrderAmount);
        targetOrder.setStatus(calculatedOrdetStatusAndAmount.targetOrderStatus);
        targetOrder.setCompletedDtm(now);
        orderService.save(targetOrder);

        candidateOrder.setAmountRemaining(calculatedOrdetStatusAndAmount.candidateReminingOrderAmount);
        candidateOrder.setStatus(calculatedOrdetStatusAndAmount.candidateOrderStatus);
        candidateOrder.setCompletedDtm(now);
        orderService.save(candidateOrder);

        //wallet balance update
        BigDecimal targetUsingBalance = targetOrder.getOrderType().equals(OrderType.SELL) ? calculatedOrdetStatusAndAmount.targetAmount : calculatedOrdetStatusAndAmount.targetAmount.multiply(targetOrder.getPrice());
        walletService.decreaseUsingBalance(
                targetOrder.getUserId()
                , targetOrder.getFromCoin().getName()
                , WalletUtil.scale(targetUsingBalance)
        );

        BigDecimal candidateUsingBalance = candidateOrder.getOrderType().equals(OrderType.SELL) ? calculatedOrdetStatusAndAmount.candidateAmount : calculatedOrdetStatusAndAmount.candidateAmount.multiply(calculatedOrdetStatusAndAmount.candidatePrice);
        walletService.decreaseUsingBalance(
                candidateOrder.getUserId()
                , candidateOrder.getFromCoin().getName()
                , WalletUtil.scale(candidateUsingBalance)
        );

        MyHistoryOrder myHistoryOrder = walletService.increaseAvailableBalanceAndFeeBalance(
                targetOrder
                , candidateOrder
                , targetOrder.getToCoin()
                , calculatedOrdetStatusAndAmount.targetAmount
                , calculatedOrdetStatusAndAmount.targetPrice
        );
        myHistoryOrderService.registMyHistory(myHistoryOrder);

        MyHistoryOrder candidateHistoryOrder = walletService.increaseAvailableBalanceAndFeeBalance(
                candidateOrder
                , targetOrder
                , candidateOrder.getToCoin()
                , calculatedOrdetStatusAndAmount.candidateAmount
                , calculatedOrdetStatusAndAmount.candidatePrice
        );
        myHistoryOrderService.registMyHistory(candidateHistoryOrder);

        marketHistoryOrderService.registMarketHistory(
                targetOrder
                , calculatedOrdetStatusAndAmount.targetAmount
                , calculatedOrdetStatusAndAmount.targetPrice
                , candidateOrder
                , calculatedOrdetStatusAndAmount.candidateAmount
                , calculatedOrdetStatusAndAmount.candidatePrice
        );

        return myHistoryOrder;
    }

    @HardTransational
    public TradeStatusDTO trade(Order order) {
        CoinEnum selectedCoinEnum = OrderType.BUY.equals(order.getOrderType()) ? order.getToCoin().getName() : order.getFromCoin().getName();

        final List<Order> candidateOrders = orderService.getTradeCandidateOrders(order);
        if (candidateOrders == null) {
            return TradeStatusDTO.builder()
                    .tradeStatusEnum(TradeStatusEnum.SHOULD_TRADED_BATCH_TRADED)
                    .order(order)
                    .build();
        }

        Map<String, MyHistoryOrder> tradedOrdersMap = new HashMap<>();
        List<MyHistoryOrder> tradedOrders = new ArrayList<>();
        for (Order candidateOrder : candidateOrders) {
            //trade
            MyHistoryOrder myHistoryOrder = tradePartial(order, candidateOrder);
            if (myHistoryOrder == null) {
                break;
            }

            //MERGE BY UNIT PRICE
            String key = myHistoryOrder.getPrice().toPlainString();
            if (tradedOrdersMap.containsKey(key)) {
                MyHistoryOrder _myHistoryOrder = tradedOrdersMap.get(key);
                _myHistoryOrder.setAmount(_myHistoryOrder.getAmount().add(myHistoryOrder.getAmount()));
                tradedOrdersMap.put(key, _myHistoryOrder);
            } else {
                tradedOrdersMap.put(key, myHistoryOrder);
            }

            //*important!!
            entityManager.flush();
            entityManager.clear();
        }

        if (tradedOrdersMap.size() > 0) {
            tradedOrders = new ArrayList(tradedOrdersMap.values());
        }

        TradeStatusDTO tradeStatusDTO = TradeStatusDTO.builder()
                .tradeStatusEnum(TradeStatusEnum.SHOULD_TRADED_PARTIAL)
                .order(order)
                .tradedOrders(tradedOrders)
                .build();

        //mq publish
        mqPublisher.websockMessagePublish(
                MessagePacket.builder()
                        .cmd(CommandEnum.TRADE)
                        .scope(PacketScopeEnum.PUBLIC)
                        .userId(order.getUserId())
                        //.data(tradeStatusDTO)
                        /*.coinAvgPrice(
                            coinService.getCoinAvgPrice(
                                CoinDTO.ReqCoinAvgPrice.builder()
                                        .coin(selectedCoinEnum)
                                        .build())
                        )*/
                        .build()
        );

        return tradeStatusDTO;
    }
}
