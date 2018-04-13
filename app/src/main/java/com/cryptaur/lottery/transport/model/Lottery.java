package com.cryptaur.lottery.transport.model;

public enum Lottery {
    _4of20, _5of36, _6of42;

    public static Lottery ofId(int id){
        return values()[id - 1];
    }
}
