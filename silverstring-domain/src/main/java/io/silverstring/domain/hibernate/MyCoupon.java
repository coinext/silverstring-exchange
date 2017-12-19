package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(MyCouponPK.class)
public class MyCoupon {

    @Id
    Long id;

    @Id
    Long userId;

    @OneToOne
    @JoinColumn(name="couponId")
    private Coupon coupon;

    LocalDateTime regDtm;
    LocalDateTime usedBeginDtm;
    LocalDateTime expireDtm;
}
