package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Coin;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class CoinDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResInfo {
        private List<Coin> infos;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqCoinAvgPrice {
        private CoinEnum coin;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResCoinAvgPrice {
        private Coin coinInfo;
        private CoinEnum coin;
        private Long totalTradeAmount24h;
        private Long price;
        private Long gapPrice;
        private String marker; //-,+,
        private Float changePercent;
    }
}
