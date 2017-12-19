package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class MarketHistoryOrderPK implements Serializable {
    @Id
    Long id;

    @Id
    Long userId;

    @Id
    Long orderId;
}
