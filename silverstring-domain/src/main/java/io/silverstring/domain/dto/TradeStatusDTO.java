package io.silverstring.domain.dto;

import io.silverstring.domain.enums.TradeStatusEnum;
import io.silverstring.domain.hibernate.MyHistoryOrder;
import io.silverstring.domain.hibernate.Order;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TradeStatusDTO {
    private TradeStatusEnum tradeStatusEnum;
    private Order order;
    private List<MyHistoryOrder> tradedOrders;
}
