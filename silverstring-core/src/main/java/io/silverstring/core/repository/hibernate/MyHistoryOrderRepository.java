package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.hibernate.MyHistoryOrder;
import io.silverstring.domain.hibernate.MyHistoryOrderPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MyHistoryOrderRepository extends JpaRepository<MyHistoryOrder, MyHistoryOrderPK> {

    @Query("SELECT p FROM MyHistoryOrder p WHERE (p.fromCoin.name = :coinName or p.toCoin.name = :coinName) and p.userId = :userId ORDER BY p.id DESC")
    Page<MyHistoryOrder> findAllByUserIdAndFromCoinOrderById(@Param("userId") Long userId, @Param("coinName") CoinEnum coinName, Pageable pageable);

    Page<MyHistoryOrder> findAllByUserIdOrderById(@Param("userId") Long userId, Pageable pageable);
}
