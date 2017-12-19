package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.StatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(TransactionPK.class)
public class Transaction {

    @Id
    private String id;

    @Id
    private Long userId;

    @OneToOne
    @JoinColumn(name="coinName")
    private Coin coin;

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    private String txId;

    private String address;
    private String tag;
    private String bankNm;
    private String recvNm;
    private BigDecimal amount;
    private LocalDateTime regDt;
    private LocalDateTime completeDtm;
    private Long confirmation;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private String reason;
}
