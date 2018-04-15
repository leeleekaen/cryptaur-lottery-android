package com.cryptaur.lottery.transport.model;

public enum Lottery {
    _4of20, _5of36, _6of42;

    public static Lottery ofId(int id) {
        return values()[id - 1];
    }

    public int getNumbersAmount() {
        switch (this) {
            case _4of20:
                return 4;

            case _5of36:
                return 5;

            case _6of42:
                return 6;

            default:
                throw new IllegalArgumentException("not implemented for: " + name());
        }
    }

    public int getMaxValue(){
        switch (this) {
            case _4of20:
                return 20;

            case _5of36:
                return 36;

            case _6of42:
                return 42;

            default:
                throw new IllegalArgumentException("not implemented for: " + name());
        }
    }
}
