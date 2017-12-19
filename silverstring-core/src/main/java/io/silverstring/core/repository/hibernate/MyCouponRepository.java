package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.MyCoupon;
import io.silverstring.domain.hibernate.MyCouponPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MyCouponRepository extends JpaRepository<MyCoupon, MyCouponPK> {
}
