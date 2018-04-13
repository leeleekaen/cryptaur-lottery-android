package com.cryptaur.lottery.transport.model;

public class CurrentDraws {
    public final Draw[] draws;
    public final long timestamp;

    public CurrentDraws(Draw[] draws) {
        this.draws = draws;
        timestamp = System.currentTimeMillis();
    }

    public Draw getDraw(Lottery lottery) {
        for (Draw draw : draws) {
            if (draw.lottery == lottery) {
                return draw;
            }
        }
        return null;
    }

    public Lottery[] getAvailableLotteries() {
        Lottery[] lotteries = new Lottery[draws.length];
        for (int i = 0; i < lotteries.length; i++) {
            lotteries[i] = draws[i].lottery;
        }
        return lotteries;
    }
}
