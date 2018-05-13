package com.cryptaur.lottery.transport.model;

import java.io.Serializable;

public class DrawId implements Serializable {
    public final Lottery lottery;
    public final int number;

    public DrawId(Lottery lottery, int number) {
        this.lottery = lottery;
        this.number = number;
    }
}
