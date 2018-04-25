package com.cryptaur.lottery.transport.model;

public class WinTicketsRequest {
    public final int start;

    public final int amount;

    public final Draw draw;

    public WinTicketsRequest(int start, int amount, Draw draw) {
        this.start = start;
        this.amount = amount;
        this.draw = draw;
    }
}