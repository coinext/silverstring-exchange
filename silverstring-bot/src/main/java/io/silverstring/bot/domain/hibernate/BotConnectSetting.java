package io.silverstring.bot.domain.hibernate;

import io.silverstring.bot.enums.ConnectStatusEnum;
import io.silverstring.bot.enums.ServiceEnum;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
public class BotConnectSetting {
    @Id
    @GeneratedValue
    Long id;

    Long userId;

    @Enumerated(EnumType.STRING)
    ServiceEnum service;

    String connectKey;
    String secretKey;
    LocalDateTime regDtm;

    @Enumerated(EnumType.STRING)
    ConnectStatusEnum status;
}
