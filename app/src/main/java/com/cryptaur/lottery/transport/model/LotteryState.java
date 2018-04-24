package com.cryptaur.lottery.transport.model;

public enum LotteryState {
    Normal, Freeze, Disposed, Unknown;

    public static LotteryState valueOfCaseInsensitive(String search) {
        if (search != null)
            for (LotteryState each : values()) {
                if (each.name().compareToIgnoreCase(search) == 0) {
                    return each;
                }
            }
        return Unknown;
    }
}
