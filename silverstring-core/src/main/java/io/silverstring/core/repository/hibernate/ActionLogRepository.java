package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.hibernate.ActionLog;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    Page<ActionLog> findAllByUserIdOrderByRegDtmDesc(Long userId, Pageable pageable);
}
