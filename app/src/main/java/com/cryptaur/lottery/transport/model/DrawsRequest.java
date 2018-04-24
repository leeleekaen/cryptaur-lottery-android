package com.cryptaur.lottery.transport.model;

public class DrawsRequest {
    public final Lottery lottery;
    public final int start;
    public final int count;

    public DrawsRequest(Lottery lottery, int start, int count) {
        this.lottery = lottery;
        this.start = start;
        this.count = count;
    }
}
