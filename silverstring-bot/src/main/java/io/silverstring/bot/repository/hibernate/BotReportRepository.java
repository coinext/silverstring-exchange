package io.silverstring.bot.repository.hibernate;

import io.silverstring.bot.domain.hibernate.BotReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BotReportRepository extends JpaRepository<BotReport, Long> {
    Page<BotReport> findAllByUserId(Long userId, Pageable pageable);
}
