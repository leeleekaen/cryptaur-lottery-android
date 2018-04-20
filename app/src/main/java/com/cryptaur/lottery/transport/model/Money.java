package com.cryptaur.lottery.transport.model;

import java.math.BigInteger;

public class Money {
    public final BigInteger amount;
    public final BigInteger fee;
    public final long timestamp;

    public Money(BigInteger amount, BigInteger fee) {
        this.amount = amount;
        this.fee = fee;
        timestamp = System.currentTimeMillis();
    }

    public long age() {
        return System.currentTimeMillis() - timestamp;
    }
}
