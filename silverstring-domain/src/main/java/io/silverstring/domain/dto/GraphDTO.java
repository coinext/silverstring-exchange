package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CoinEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class GraphDTO {

    @Data
    public static class ReqGraphData24H {
        private CoinEnum coin;
    }

    @Data
    public static class ResGraphData24H {
        private List<String> dates;
        private List<Long> prices;
        private Long minPrice;
        private Long maxPrice;
        private Long avgPrice;
    }
}
