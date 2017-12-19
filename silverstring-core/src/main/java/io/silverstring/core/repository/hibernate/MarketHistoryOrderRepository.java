package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.dto.ChartDTO;
import io.silverstring.domain.dto.MaxMinMarketHistoryOrderDTO;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.enums.OrderType;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.hibernate.MarketHistoryOrderPK;
import io.silverstring.domain.hibernate.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface MarketHistoryOrderRepository extends JpaRepository<MarketHistoryOrder, MarketHistoryOrderPK> {

    @Query("SELECT new io.silverstring.domain.dto.MaxMinMarketHistoryOrderDTO(min(p.price), max(p.price)) FROM MarketHistoryOrder p WHERE (p.fromCoin.name = :coinName or p.toCoin.name = :coinName) AND (p.completedDtm >= :startDt AND p.completedDtm < :endDt)")
    MaxMinMarketHistoryOrderDTO getMaxMinMarketHistoryOrderBetweenStartDtAndEndDt(@Param("coinName") CoinEnum coinName, @Param("startDt") LocalDateTime startDt, @Param("endDt") LocalDateTime endDt);

    @Query("SELECT p FROM MarketHistoryOrder p WHERE (p.fromCoin.name = :coinName or p.toCoin.name = :coinName) AND (p.completedDtm >= :startDt AND p.completedDtm < :endDt) order by p.completedDtm DESC")
    List<MarketHistoryOrder> getMarketHistoryOrderBetweenStartDtAndEndDtOrderDesc(@Param("coinName") CoinEnum coinName, @Param("startDt") LocalDateTime startDt, @Param("endDt") LocalDateTime endDt);

    @Query("SELECT p FROM MarketHistoryOrder p WHERE (p.fromCoin.name = :coinName or p.toCoin.name = :coinName) ORDER BY p.id DESC")
    Page<MarketHistoryOrder> findAllByFromCoinOrderById(@Param("coinName") CoinEnum coinName, Pageable pageable);

    @Query("SELECT p FROM MarketHistoryOrder p WHERE (p.fromCoin.name = :coinName or p.toCoin.name = :coinName) and p.orderType = 'SELL' ORDER BY p.id DESC")
    Page<MarketHistoryOrder> getCandidatesToAvgCoinPrice(@Param("coinName") CoinEnum coinName, Pageable pageable);

    @Query("SELECT p FROM MarketHistoryOrder p WHERE (p.fromCoin.name = :coinName or p.toCoin.name = :coinName) AND (p.completedDtm >= :startDt AND p.completedDtm < :endDt)")
    List<MarketHistoryOrder> getCandidatesToTradeCoinAmount(@Param("coinName") CoinEnum coinName, @Param("startDt") LocalDateTime startDt, @Param("endDt") LocalDateTime endDt);

}
