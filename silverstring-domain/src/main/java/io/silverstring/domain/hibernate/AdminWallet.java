package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.WalletType;
import lombok.Data;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@IdClass(AdminWalletPK.class)
public class AdminWallet {

    @Id
    @Enumerated(EnumType.STRING)
    private CoinEnum coinName;

    @Id
    @Enumerated(EnumType.STRING)
    private WalletType type;

    private String address;
    private String tag;

    private String bankName;
    private String bankCode;
    private String recvCorpNm;

    @Digits(integer=24, fraction=8)
    @DecimalMin("0.00000000")
    private BigDecimal usingBalance;

    @Digits(integer=24, fraction=8)
    @DecimalMin("0.00000000")
    private BigDecimal availableBalance;

    private LocalDateTime regDt;
}
