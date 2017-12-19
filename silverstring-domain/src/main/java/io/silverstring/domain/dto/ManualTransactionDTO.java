package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.ManualTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ManualTransactionDTO {

    @Data
    public static class ReqWithdrawalAdd {
        private String id;
        private Long userId;
        private CoinEnum coin;
        private CategoryEnum category;
        private String address;
        private String tag;
        private String bankNm;
        private String recvNm;
        private String depositDvcd;
        private BigDecimal amount;
        private LocalDateTime regDt;
        private String reason;
    }

    @Data
    public static class ReqWithdrawalEdit {
        private String id;
        private Long userId;
        private CoinEnum coin;
        private CategoryEnum category;
        private String address;
        private String tag;
        private String bankNm;
        private String recvNm;
        private String depositDvcd;
        private BigDecimal amount;
        private LocalDateTime regDt;
        private StatusEnum status;
        private String reason;
    }

    @Data
    public static class ReqWithdrawalDel {
        private String id;
        private Long userId;
    }

    @Data
    public static class ReqDepositKrwAdd {
        private String id;
        private Long userId;
        private CoinEnum coin;
        private CategoryEnum category;
        private String address;
        private String tag;
        private String bankNm;
        private String recvNm;
        private String depositDvcd;
        private BigDecimal amount;
        private LocalDateTime regDt;
        private String reason;
    }

    @Data
    public static class ReqDepositKrwEdit {
        private String id;
        private Long userId;
        private CoinEnum coin;
        private CategoryEnum category;
        private String address;
        private String tag;
        private String bankNm;
        private String recvNm;
        private String depositDvcd;
        private BigDecimal amount;
        private LocalDateTime regDt;
        private StatusEnum status;
        private String reason;
    }

    @Data
    public static class ReqDepositKrwDel {
        private String id;
        private Long userId;
    }

    @Data
    public static class ReqManualTransaction {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ResManualTransaction {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<ManualTransaction> contents;
    }
}
