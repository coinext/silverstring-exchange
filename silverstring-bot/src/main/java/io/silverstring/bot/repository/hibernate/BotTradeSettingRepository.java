package io.silverstring.bot.repository.hibernate;

import io.silverstring.bot.domain.hibernate.BotTradeSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BotTradeSettingRepository extends JpaRepository<BotTradeSetting, Long> {
    Page<BotTradeSetting> findAllByUserId(Long userId, Pageable pageable);
}
