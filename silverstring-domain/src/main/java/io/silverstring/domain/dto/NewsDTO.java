package io.silverstring.domain.dto;

import io.silverstring.domain.hibernate.News;
import lombok.Data;

import java.util.List;

public class NewsDTO {

    @Data
    public static class ReqAdd {
        private Long id;

        private String title;
        private String url;
    }

    @Data
    public static class ReqEdit {
        private Long id;

        private String title;
        private String url;
    }

    @Data
    public static class ReqDel {
        private Long id;
    }

    @Data
    public static class ReqNews {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResNews {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<News> contents;
    }
}
