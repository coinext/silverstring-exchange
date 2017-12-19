package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.OrderStatus;
import io.silverstring.domain.enums.OrderType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="`Order`")
public class Order implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @OneToOne
    @JoinColumn(name="fromCoinName")
    private Coin fromCoin;

    @OneToOne
    @JoinColumn(name="toCoinName")
    private Coin toCoin;

    private LocalDateTime regDtm;

    @Transient
    private String regDtText;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @DecimalMin("0.00000000")
    private BigDecimal amount;

    @Transient
    private String amountText;

    @DecimalMin("0.00000000")
    private BigDecimal amountRemaining;

    @Transient
    private String amountRemainingText;

    @DecimalMin("0.00000000")
    private BigDecimal price;

    @Transient
    private String priceText;

    @DecimalMin("0.00000000")
    @Transient
    private BigDecimal totalPrice;

    @Transient
    private String totalPriceText;

    private LocalDateTime completedDtm;
    private LocalDateTime cancelDtm;

    @Transient
    public BigDecimal getTotalPrice() {
        return amount.multiply(price).setScale(8, RoundingMode.DOWN);
    }

    @Transient
    public String getAmountText() {
        return amount.toPlainString();
    }

    @Transient
    public String getAmountRemainingText() {
        return amountRemaining.toPlainString();
    }

    @Transient
    public String getPriceText() {
        return price.toPlainString();
    }

    @Transient
    public String getTotalPriceText() {
        return getTotalPrice().setScale(8, RoundingMode.DOWN).toPlainString();
    }
}
