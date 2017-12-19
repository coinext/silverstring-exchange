package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.LevelEnum;
import io.silverstring.domain.hibernate.Level;
import io.silverstring.domain.hibernate.LevelPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LevelRepository extends JpaRepository<Level, LevelPK> {
    Level findOneByCoinNameAndLevel(CoinEnum coinName, LevelEnum level);
    Page<Level> findAll(Pageable pageable);
}
