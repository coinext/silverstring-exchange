package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Notice;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class NoticeDTO {

    @Data
    public static class ReqAdd {
        private Long id;

        private String title;
        private String content;
        private String url;
    }

    @Data
    public static class ReqEdit {
        private Long id;

        private String title;
        private String content;
        private String url;
    }

    @Data
    public static class ReqDel {
        private Long id;
    }

    @Data
    public static class ReqNotice {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResNotice {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Notice> contents;
    }
}
