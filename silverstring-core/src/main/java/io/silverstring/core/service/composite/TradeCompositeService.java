package io.silverstring.core.service.composite;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.exception.ExchangeException;
import io.silverstring.core.provider.MqPublisher;
import io.silverstring.core.repository.hibernate.OrderRepository;
import io.silverstring.core.service.*;
import io.silverstring.domain.dto.OrderDTO;
import io.silverstring.domain.dto.TradeStatusDTO;
import io.silverstring.domain.enums.CodeEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.enums.OrderType;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Order;
import io.silverstring.domain.hibernate.Wallet;
import io.silverstring.domain.util.CompareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class TradeCompositeService {

    private final TradeService tradeService;
    private final OrderService orderService;
    private final CoinService coinService;
    private final WalletService walletService;
    private final AdminWalletService adminWalletService;
    private final MqPublisher mqPublisher;

    private final OrderRepository orderRepository;

    @Autowired
    public TradeCompositeService(TradeService tradeService, OrderService orderService, CoinService coinService, WalletService walletService, MqPublisher mqPublisher, AdminWalletService adminWalletService, OrderRepository orderRepository) {
        this.tradeService = tradeService;
        this.orderService = orderService;
        this.coinService = coinService;
        this.walletService = walletService;
        this.mqPublisher = mqPublisher;
        this.adminWalletService = adminWalletService;
        this.orderRepository = orderRepository;
    }

    @HardTransational
    public Order cancel(Long userId, Long orderId) {
        Optional<Order> existOrder = orderRepository.findOneByIdAndUserIdAndStatus(orderId, userId, OrderStatus.PLACED);
        if (!existOrder.isPresent()) {
            throw new ExchangeException(CodeEnum.ORDER_CANCEL_FAIL);
        }

        Order order = existOrder.get();
        order.setCancelDtm(LocalDateTime.now());
        order.setStatus(OrderStatus.CANCEL);

        if (OrderType.BUY.equals(order.getOrderType())) {
            BigDecimal canceledBalance = order.getAmountRemaining().multiply(order.getPrice()).setScale(8);
            CoinEnum cancelCoinEnum = order.getFromCoin().getName();

            walletService.decreaseUsingBalance(
                    userId
                    , cancelCoinEnum
                    , canceledBalance
            );

            walletService.increaseAvailableBalance(
                    userId
                    , cancelCoinEnum
                    , canceledBalance
            );
        } else if (OrderType.SELL.equals(order.getOrderType())) {
            BigDecimal canceledBalance = order.getAmountRemaining();
            CoinEnum cancelCoinEnum = order.getFromCoin().getName();

            walletService.decreaseUsingBalance(
                    userId
                    , cancelCoinEnum
                    , canceledBalance
            );

            walletService.increaseAvailableBalance(
                    userId
                    , cancelCoinEnum
                    , canceledBalance
            );
        } else {
            throw new ExchangeException(CodeEnum.INVALID_ORDER_TYPE);
        }

        return order;
    }

    @HardTransational
    public TradeStatusDTO buy(Long userId, OrderDTO.ReqOrder request) {

        //validate
        if (!OrderType.BUY.equals(request.getOrderType())) {
            throw new ExchangeException(CodeEnum.INVAILD_ORDER_TYPE);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getPrice()))) {
            throw new ExchangeException(CodeEnum.UNDER_PRICE_ZERO);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getAmount()))) {
            throw new ExchangeException(CodeEnum.UNDER_AMOUNT_ZERO);
        }

        Coin fromCoin = coinService.getActiveCoin(request.getFromCoin()); //KRW
        Coin toCoin = coinService.getActiveCoin(request.getToCoin());

        BigDecimal krwBalance = request.getPrice().multiply(request.getAmount());
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(krwBalance, fromCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(request.getAmount(), toCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        BigDecimal fee = krwBalance.multiply(toCoin.getTradingFeePercent().divide(new BigDecimal(100))).setScale(8, RoundingMode.DOWN);
        Order order = orderService.buy(userId, request.getPrice(), request.getAmount(), fee, fromCoin, toCoin);

        Wallet fromWallet = walletService.get(userId, fromCoin);
        fromWallet.setAvailableBalance(fromWallet.getAvailableBalance().subtract(krwBalance));
        fromWallet.setUsingBalance(fromWallet.getUsingBalance().add(krwBalance));

        return tradeService.trade(order);
    }

    @HardTransational
    public TradeStatusDTO sell(Long userId, OrderDTO.ReqOrder request) {
        //validate
        if (!OrderType.SELL.equals(request.getOrderType())) {
            throw new ExchangeException(CodeEnum.INVAILD_ORDER_TYPE);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getPrice()))) {
            throw new ExchangeException(CodeEnum.UNDER_PRICE_ZERO);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compareToZero(request.getAmount()))) {
            throw new ExchangeException(CodeEnum.UNDER_AMOUNT_ZERO);
        }

        Coin fromCoin = coinService.getActiveCoin(request.getFromCoin());
        Coin toCoin = coinService.getActiveCoin(request.getToCoin()); //KRW

        BigDecimal krwBalance = request.getPrice().multiply(request.getAmount());
        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(krwBalance, fromCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        if (CompareUtil.Condition.LT.equals(CompareUtil.compare(request.getAmount(), toCoin.getTradingMinAmount()))) {
            throw new ExchangeException(CodeEnum.AMOUNT_IS_UNDER_MIN_AMOUNT);
        }

        BigDecimal fee = krwBalance.multiply(toCoin.getTradingFeePercent()).divide(new BigDecimal(100)).setScale(8, RoundingMode.DOWN);
        Order order = orderService.sell(userId, request.getPrice(), request.getAmount(), fee, fromCoin, toCoin);


        Wallet fromWallet = walletService.get(userId, fromCoin);
        BigDecimal coinBalance = request.getAmount();
        fromWallet.setAvailableBalance(fromWallet.getAvailableBalance().subtract(coinBalance));
        fromWallet.setUsingBalance(fromWallet.getUsingBalance().add(coinBalance));

        return tradeService.trade(order);
    }
}
