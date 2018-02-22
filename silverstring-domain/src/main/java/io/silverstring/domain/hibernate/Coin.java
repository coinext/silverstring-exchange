package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.enums.CoinEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Coin {

    @Id
    @Enumerated(EnumType.STRING)
    private CoinEnum name;

    private String hanName;

    private String mark;
    private String unit;
    @Column(name = "display_priority")
    private Long displayPriority;

    private String logoUrl;

    private LocalDateTime regDtm;

    @Enumerated(EnumType.STRING)
    private ActiveEnum active;

    private BigDecimal withdrawalMinAmount;
    private BigDecimal withdrawalAutoAllowMaxAmount;
    private BigDecimal withdrawalFeeAmount;

    private BigDecimal autoCollectMinAmount;

    private BigDecimal tradingFeePercent;
    private BigDecimal tradingMinAmount;

    private BigDecimal marginTradingFeePercent;

    private Integer depositScanPageOffset;
    private Integer depositScanPageSize;

    private Long depositAllowConfirmation;

    public Coin() {

    }

    public Coin(CoinEnum coinEnum) {
        this.name = coinEnum;
    }

    public Coin(String coinName) {
        this.name = CoinEnum.valueOf(coinName);
    }
}
