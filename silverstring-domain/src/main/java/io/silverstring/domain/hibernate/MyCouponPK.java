package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Data
public class MyCouponPK implements Serializable {

    @Id
    Long id;

    @Id
    Long userId;

    @OneToOne
    @JoinColumn(name="couponId")
    Coupon coupon;
}
