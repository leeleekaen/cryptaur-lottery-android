package com.cryptaur.lottery.transport.model;

import java.math.BigInteger;

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

    public BigInteger getTotalJackpot() {
        BigInteger jackpot = BigInteger.ZERO;

        for (Draw draw : draws) {
            jackpot = jackpot.add(draw.jackpot);
        }
        return jackpot;
    }

    public DrawIds latestPlayedDraws() {
        DrawIds result = new DrawIds();

        for (Draw draw : draws) {
            if (draw.number > 1)
                result.add(new DrawId(draw.lottery, draw.number - 1));
        }

        return result;
    }
}