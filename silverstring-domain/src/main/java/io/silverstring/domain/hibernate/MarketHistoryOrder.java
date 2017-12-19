package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.enums.OrderType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@IdClass(MarketHistoryOrderPK.class)
public class MarketHistoryOrder implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    Long id;

    @Id
    @Column(name = "user_id", nullable = false)
    Long userId;

    @Id
    @Column(name = "order_id", nullable = false)
    Long orderId;

    @OneToOne
    @JoinColumn(name="fromCoinName")
    private Coin fromCoin;

    @OneToOne
    @JoinColumn(name="toCoinName")
    private Coin toCoin;

    private LocalDateTime regDtm;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @DecimalMin("0.00000000")
    private BigDecimal amount;

    @DecimalMin("0.00000000")
    private BigDecimal price;

    @DecimalMin("0.00000000")
    @Transient
    private BigDecimal totalPrice;

    private String dt;
    private LocalDateTime completedDtm;

    @Transient
    public BigDecimal getTotalPrice() {
        return amount.multiply(price);
    }
}
