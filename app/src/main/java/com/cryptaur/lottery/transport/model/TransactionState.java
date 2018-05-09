package com.cryptaur.lottery.transport.model;

public class TransactionState {
    public final Transaction transaction;
    public final State state;

    public TransactionState(Transaction transaction, State state) {
        this.transaction = transaction;
        this.state = state;
    }

    public enum State {Fail, Success, Pending}
}
