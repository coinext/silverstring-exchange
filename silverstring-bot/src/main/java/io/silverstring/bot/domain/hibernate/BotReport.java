package io.silverstring.bot.domain.hibernate;

import io.silverstring.bot.enums.ServiceEnum;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
public class BotReport {
    @Id
    @GeneratedValue
    Long id;

    Long userId;

    @Enumerated(EnumType.STRING)
    ServiceEnum service;
    LocalDateTime regDtm;

    @Digits(integer=24, fraction=8)
    BigDecimal assets;

    @Digits(integer=24, fraction=8)
    BigDecimal profit;

    String action;
}
