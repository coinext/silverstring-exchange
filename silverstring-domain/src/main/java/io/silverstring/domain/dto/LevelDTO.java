package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.LevelEnum;
import io.silverstring.domain.hibernate.Level;
import io.silverstring.domain.hibernate.Notice;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class LevelDTO {

    @Data
    public static class ReqAdd {
        private String coinName;

        private LevelEnum level;
        private BigDecimal onceAmount;
        private BigDecimal onedayAmount;
    }

    @Data
    public static class ReqEdit {
        private String coinName;

        private LevelEnum level;
        private BigDecimal onceAmount;
        private BigDecimal onedayAmount;
    }

    @Data
    public static class ReqDel {
        private String coinName;
    }

    @Data
    public static class ReqLevel {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResLevel {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Level> contents;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupInfo {
        private CoinEnum coinName;
        private Level level1;
        private Level level2;
        private Level level3;
    }
}
