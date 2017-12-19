package io.silverstring.domain.dto;

import io.silverstring.domain.util.DataUtil;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GroupOrderDTO {
    private String price;
    private String amount;
    private String totalPrice;

    public GroupOrderDTO(BigDecimal price, BigDecimal amount, BigDecimal totalPrice) {
        this.price = DataUtil.decimal(price);
        this.amount = DataUtil.decimal(amount);
        this.totalPrice = DataUtil.decimal(totalPrice);
    }
}
