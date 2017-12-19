package io.silverstring.domain.util;

import java.math.BigDecimal;

public class CompareUtil {

    public static enum Condition {
        LT, GT, EQ, UNKNOWN
    }

    public static Condition compare(BigDecimal target1, BigDecimal target2) {
        if (target1.compareTo(target2) == -1) {
            return Condition.LT;
        }

        if (target1.compareTo(target2) == 0) {
            return Condition.EQ;
        }

        if (target1.compareTo(target2) == 1) {
            return Condition.GT;
        }

        return Condition.UNKNOWN;
    }

    public static Condition compareToZero(BigDecimal target1) {
        return compare(target1, BigDecimal.ZERO);
    }
}
