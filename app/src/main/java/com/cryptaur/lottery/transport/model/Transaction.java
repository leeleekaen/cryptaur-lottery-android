package com.cryptaur.lottery.transport.model;

public class Transaction {
    public final String transactionHash;
    public final Object transactionObject;

    public Transaction(String transactionHash, Object transactionObject) {
        this.transactionHash = transactionHash;
        this.transactionObject = transactionObject;
    }
}
