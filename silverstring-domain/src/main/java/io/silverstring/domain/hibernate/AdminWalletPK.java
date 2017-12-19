package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.WalletType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class AdminWalletPK implements Serializable {

    @Id
    @Enumerated(EnumType.STRING)
    private CoinEnum coinName;

    @Id
    @Enumerated(EnumType.STRING)
    private WalletType type;
}
