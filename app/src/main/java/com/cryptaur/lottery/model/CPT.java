package com.cryptaur.lottery.model;

import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;

public class CPT {
    public static String toDecimalString(BigInteger amount, int minDigitsAfterPoint) {
        return Strings.toDecimalString(amount, 8, minDigitsAfterPoint, ".", ",");
    }

    public static String toDecimalString(BigInteger amount) {
        return Strings.toDecimalString(amount, 8, 0, ".", ",");
    }
}
