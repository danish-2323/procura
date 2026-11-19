package com.procura.shared.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    public static final String DEFAULT_CURRENCY = "INR";
    public static final int MONEY_SCALE = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private MoneyUtils() {}

    public static BigDecimal round(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING_MODE);
        }
        return amount.setScale(MONEY_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal computeTax(BigDecimal subtotal, BigDecimal taxRate) {
        if (subtotal == null || taxRate == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING_MODE);
        }
        return round(subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 4, ROUNDING_MODE));
    }
}
