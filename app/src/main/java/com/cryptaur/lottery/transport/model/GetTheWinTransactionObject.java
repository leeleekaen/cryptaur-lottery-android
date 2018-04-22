package com.cryptaur.lottery.transport.model;

public class GetTheWinTransactionObject {
    public final Money moneyAmount;
    public final long timestamp;

    public GetTheWinTransactionObject(Money moneyAmount, long timestamp) {
        this.moneyAmount = moneyAmount;
        this.timestamp = timestamp;
    }
}
