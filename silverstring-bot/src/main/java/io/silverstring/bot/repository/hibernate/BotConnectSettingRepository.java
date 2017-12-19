package io.silverstring.bot.repository.hibernate;

import io.silverstring.bot.domain.hibernate.BotConnectSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BotConnectSettingRepository extends JpaRepository<BotConnectSetting, Long> {
    Page<BotConnectSetting> findAllByUserId(Long userId, Pageable pageable);
}