package com.cryptaur.lottery.transport.model;

public class TicketsToLoad {
    public final Lottery lottery;
    public final int start;
    public final int amount;

    public TicketsToLoad(Lottery lottery, int start, int amount) {
        this.lottery = lottery;
        this.start = start;
        this.amount = amount;
    }
}
