package io.silverstring.domain.dto;

import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.enums.SupportTypeEnum;
import io.silverstring.domain.hibernate.Coupon;
import io.silverstring.domain.hibernate.Support;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class SupportDTO {

    @Data
    public static class ReqAdd {
        private Long id;
        private Long parentId;
        private Long userId;
        private String title;
        private String content;
        private SupportTypeEnum type;
        private LocalDateTime regDtm;
        private StatusEnum status;
        private String reason;
    }

    @Data
    public static class ReqComment {
        private Long id;
        private Long isPost;
        private Long parentId;
        private Long userId;
        private String title;
        private String content;
        private LocalDateTime regDtm;
        private StatusEnum status;
        private String reason;
    }

    @Data
    public static class ReqDel {
        private Long id;
    }

    @Data
    public static class ReqSupport {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResSupport {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Support> contents;
    }
}
