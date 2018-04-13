package com.cryptaur.lottery.util;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class StringsUnitTest {
    @Test
    public void toDecimalString_test1() {
        long value = 234_000_000_000L;
        String result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 3, ".", ",");
        assertEquals( "2,340.000", result);

        result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 0, ".", null);
        assertEquals( "2340", result);

        result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 0, ".", ",");
        assertEquals( "2,340", result);
    }

    @Test
    public void toDecimalString_test3() {
        long value = 234_010_000_000L;
        String result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 3, ".", ",");
        assertEquals("2,340.100", result);

        result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 0, ".", ",");
        assertEquals( "2,340.1", result);
    }

    @Test
    public void toDecimalString_test5() {
        long value = 3_400_000_001L;
        String result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 3, ".", ",");
        assertEquals( "34.00000001", result);

        value = 23_400_000_001L;
        result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 3, ".", ",");
        assertEquals( "234.00000001", result);
    }

    @Test
    public void toDecimalString_zero() {
        String result = Strings.toDecimalString(BigInteger.ZERO, 8, 3, ".", ",");
        assertEquals( "0.000", result);

        String result1 = Strings.toDecimalString(BigInteger.ZERO, 8, 0, ".", ",");
        assertEquals( "0", result1);
    }

    @Test
    public void toDecimalString_small() {
        long value = 10_000_001L;
        String result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 3, ".", ",");
        assertEquals( "0.10000001", result);

        value = 1_000_000L;
        result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 3, ".", ",");
        assertEquals( "0.010", result);

        result = Strings.toDecimalString(BigInteger.valueOf(value), 8, 0, ".", ",");
        assertEquals( "0.01", result);
    }
}
