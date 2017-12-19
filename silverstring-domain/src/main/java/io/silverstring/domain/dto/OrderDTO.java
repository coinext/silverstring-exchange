package io.silverstring.domain.dto;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.OrderType;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.MarketHistoryOrder;
import io.silverstring.domain.hibernate.MyHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class OrderDTO {


    @Data
    public static class ReqMyOrders {
        @NotNull
        CoinEnum fromCoin;

        Integer pageNo;
        Integer pageSize;
    }

    @Data
    @Builder
    public static class ResMyOrders {
        Integer pageTotalCnt;
        Integer pageNo;
        Integer pageSize;

        List<Order> orders;
    }

    @Data
    public static class ReqMyHistoryOrders {
        CoinEnum fromCoin;

        Integer pageNo;
        Integer pageSize;
    }

    @Data
    @Builder
    public static class ResMyHistoryOrders {
        Integer pageTotalCnt;
        Integer pageNo;
        Integer pageSize;
        List<MyHistoryOrder> historyOrders;
    }

    @Data
    public static class ReqMarketHistoryOrders {
        @NotNull
        CoinEnum fromCoin;

        Integer pageNo;
        Integer pageSize;
    }

    @Data
    @Builder
    public static class ResMarketHistoryOrders {
        List<MarketHistoryOrder> historyOrders;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqHogaOrders {
        @NotNull
        CoinEnum fromCoin;

        @NotNull
        CoinEnum toCoin;

        String sellPageNo;
        Integer sellPageSize;

        String buyPageNo;
        Integer buyPageSize;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResHogaOrders {
        List<GroupOrderDTO> buy;
        List<GroupOrderDTO> sell;
    }

    @Data
    public static class ReqOrder {
        @NotNull
        @DecimalMin("0.00000001")
        private BigDecimal price;
        private OrderType orderType;
        @NotNull
        @DecimalMin("0.00000001")
        private BigDecimal amount;
        @NotNull
        private CoinEnum fromCoin;
        @NotNull
        private CoinEnum toCoin;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResCancel {
        private Order order;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqCancel {
        @NotNull
        Long orderId;
    }
}
