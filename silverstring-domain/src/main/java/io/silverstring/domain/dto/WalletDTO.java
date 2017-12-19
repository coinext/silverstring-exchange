package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Level;
import io.silverstring.domain.hibernate.Notice;
import io.silverstring.domain.hibernate.Wallet;
import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class WalletDTO {

    @Data
    public static class ResWallet {
        Integer pageNo;
        Integer pageSize;
        Integer pageTotalCnt;
        List<Wallet> contents;
    }

    @Data
    public static class ReqWallet {
        Integer pageNo;
        Integer pageSize;
    }

    @Data
    public static class ReqAdd {

    }

    @Data
    public static class ReqEdit {

    }

    @Data
    public static class ReqDel {

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletCreateInfo {
        @NotNull
        String address;
        String tag;

        //FOR KRW
        String bankName;
        String bankCode;
        String recvCorpNm;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionInfo {
        Long userId;
        CoinEnum coinEnum;
        String address;
        CategoryEnum category;
        BigDecimal amount;
        BigInteger confirmations;
        String txid;
        String bankNm;
        String recvNm;
        LocalDateTime timereceived;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletInfos {
        private List<Info> infos;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Info {
            private Coin coin;
            private Wallet wallet;
            private Level level;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResCreateWallet {
        @NotNull
        String address;
        @Nullable
        String tag;
    }

    @Data
    public static class ReqCreateWallet {
        @NotNull
        String coinName;
    }
}
