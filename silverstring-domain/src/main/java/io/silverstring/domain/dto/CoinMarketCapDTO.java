package io.silverstring.domain.dto;

import lombok.Data;

@Data
public class CoinMarketCapDTO {

    @Data
    public static class Ticker {
        private String id;
        private String name;
        private String symbol;
        private String percent_change_24h;
        private String price_krw;
    }
}
