package com.cryptaur.lottery.transport.model;

import java.util.Locale;

public enum Lottery {
    _4of20, _5of36, _6of42;

    public static Lottery ofServerId(int id) {
        return values()[id - 1];
    }

    public String displayName() {
        return String.format(Locale.getDefault(), "%dx%d", getNumbersAmount(), getMaxValue());
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

    public int getMaxValue() {
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

    public int getServerId() {
        return ordinal() + 1;
    }

    public double[] getWinShares() {
        switch (this) {
            case _4of20:
                return new double[]{0, 0, 0.25, 0.15, 0.50};

            case _5of36:
                return new double[]{0, 0, 0.2, 0.1, 0.1, 0.5};

            case _6of42:
                return new double[]{0, 0, 0.2, 0.1, 0.05, 0.05, 0.5};

            default:
                throw new RuntimeException("Not implemented for " + name());
        }
    }
}