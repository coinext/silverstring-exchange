package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.dto.GroupOrderDTO;
import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.enums.OrderType;
import io.silverstring.domain.hibernate.Coin;
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
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOneByIdAndUserIdAndStatus(Long id, Long userId, OrderStatus status);
    Optional<Order> findOneByIdAndUserId(Long id, Long userId);
    Optional<Order> findOneById(Long id);
    Optional<Page<Order>> findAllByIdAndUserId(Long id, Long userId, Pageable pageable);
    Optional<Page<Order>> findAllByUserId(Long userId, Pageable pageable);
    Optional<List<Order>> findAllByOrderTypeAndStatusOrderByIdDesc(OrderType orderType, OrderStatus status);

    @Query("SELECT p FROM Order p WHERE p.userId = :userId and (p.fromCoin = :coin or p.toCoin = :coin) and p.status = :status ORDER BY p.id DESC")
    Page<Order> findAllByUserIdAndCoinAndStatus(
            @Param("userId") Long userId
            , @Param("coin") Coin coin
            , @Param("status") OrderStatus status
            , Pageable pageable
    );

    @Query("SELECT p FROM Order p WHERE p.userId = :userId and p.orderType = :orderType and p.fromCoin = :fromCoin and p.toCoin = :toCoin and p.status = :status ORDER BY p.id DESC")
    Page<Order> findAllByUserIdAndOrderTypeAndFromCoinAndToCoinAndStatus(
            @Param("userId") Long userId
            , @Param("orderType") OrderType orderType
            , @Param("fromCoin") Coin fromCoin
            , @Param("toCoin") Coin toCoin
            , @Param("status") OrderStatus status
            , Pageable pageable
    );


    @Query("SELECT p  FROM Order p WHERE p.amountRemaining > 0 and p.fromCoin = :fromCoin and p.toCoin = :toCoin and p.status = :orderStatus and p.orderType = :orderType and p.regDtm >= :prevOrderTime and p.price <= :price order by p.price asc")
    Optional<Page<Order>> getTradeCandidateOrdersSell(
            @Param("fromCoin") Coin fromCoin
            , @Param("toCoin") Coin toCoin
            , @Param("orderStatus") OrderStatus orderStatus
            , @Param("orderType") OrderType orderType
            , @Param("price") BigDecimal price
            , @Param("prevOrderTime") LocalDateTime prevOrderTime
            , Pageable pageable
    );

    @Query("SELECT p  FROM Order p WHERE p.amountRemaining > 0 and p.fromCoin = :fromCoin and p.toCoin = :toCoin and p.status = :orderStatus and p.orderType = :orderType and p.regDtm >= :prevOrderTime and p.price >= :price order by p.price desc")
    Optional<Page<Order>> getTradeCandidateOrdersBuy(
            @Param("fromCoin") Coin fromCoin
            , @Param("toCoin") Coin toCoin
            , @Param("orderStatus") OrderStatus orderStatus
            , @Param("orderType") OrderType orderType
            , @Param("price") BigDecimal price
            , @Param("prevOrderTime") LocalDateTime prevOrderTime
            , Pageable pageable
    );

    @Query("SELECT new io.silverstring.domain.dto.GroupOrderDTO(p.price, sum(p.amountRemaining) as amount_remaining, p.price * sum(p.amountRemaining) as totalPrice) FROM Order p WHERE p.status = :status and p.orderType = :orderType and p.fromCoin = :fromCoin and p.toCoin = :toCoin GROUP BY p.price, p.orderType, p.status ORDER BY p.price DESC")
    Page<GroupOrderDTO> getHogaOrders(
            @Param("status") OrderStatus orderStatus
            , @Param("orderType") OrderType orderType
            , @Param("fromCoin") Coin fromCoin
            , @Param("toCoin") Coin toCoin
            , Pageable pageable
    );

}
