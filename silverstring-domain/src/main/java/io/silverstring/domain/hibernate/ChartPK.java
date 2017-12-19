package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class ChartPK implements Serializable {
    @Id
    String dt;

    @Id
    @Enumerated(EnumType.STRING)
    CoinEnum coin;
}