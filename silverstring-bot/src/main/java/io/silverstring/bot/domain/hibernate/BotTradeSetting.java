package io.silverstring.bot.domain.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
@Entity
public class BotTradeSetting {
    @Id
    @GeneratedValue
    Long id;

    Long userId;
}
