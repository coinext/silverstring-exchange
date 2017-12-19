package io.silverstring.core.repository.hibernate;


import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoinRepository extends JpaRepository<Coin, CoinEnum> {
    List<Coin> findAllByOrderByDisplayPriorityAsc();
}
