package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.dto.ChartDTO;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Chart;
import io.silverstring.domain.hibernate.ChartPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChartRepository extends JpaRepository<Chart, ChartPK> {

    @Query("SELECT new io.silverstring.domain.dto.ChartDTO(p.dt, p.open, p.high, p.low, p.close) FROM Chart p where coin = :coin ORDER BY p.dt ASC")
    List<ChartDTO> findAllByOrderByDtASC(@Param("coin") CoinEnum coin);

    @Query("SELECT new io.silverstring.domain.dto.ChartDTO(p.dt, p.open, p.high, p.low, p.close) FROM Chart p where coin = :coin ORDER BY p.dt DESC")
    Page<ChartDTO> findOneFromCoinOrderByDtDesc(@Param("coin") CoinEnum coin, Pageable pageable);
}
