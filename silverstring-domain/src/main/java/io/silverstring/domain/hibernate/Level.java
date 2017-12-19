package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.LevelEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@IdClass(LevelPK.class)
public class Level {

    @Id
    @Enumerated(EnumType.STRING)
    private CoinEnum coinName;

    @Id
    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    private BigDecimal onceAmount;
    private BigDecimal onedayAmount;
}
