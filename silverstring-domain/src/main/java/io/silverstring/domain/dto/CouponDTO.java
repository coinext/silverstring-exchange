package io.silverstring.domain.dto;

import io.silverstring.domain.hibernate.Coupon;
import io.silverstring.domain.hibernate.Notice;
import lombok.Data;

import java.util.List;

public class CouponDTO {

    @Data
    public static class ReqAdd {
        private Long id;

        private String name;
        private String content;
        private String imgUrl;
        private Long expireHr;
    }

    @Data
    public static class ReqEdit {
        private Long id;

        private String name;
        private String content;
        private String imgUrl;
        private Long expireHr;
    }

    @Data
    public static class ReqDel {
        private Long id;
    }

    @Data
    public static class ReqCoupon {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResCoupon {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Coupon> contents;
    }
}
