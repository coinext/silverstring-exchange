package io.silverstring.core.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;


@UtilityClass
public class CalculateFeeUtil {

    public BigDecimal getRealAmount(BigDecimal amount, BigDecimal feePercent) {
        return WalletUtil.scale(amount.subtract(getFeeAmount(amount, feePercent)));
    }

    public BigDecimal getFeeAmount(BigDecimal amount, BigDecimal feePercent) {
        return WalletUtil.scale(feePercent.divide(new BigDecimal("100")).multiply(amount));
    }
}
