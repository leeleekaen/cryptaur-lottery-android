package com.cryptaur.lottery.transport.model;

import android.support.annotation.NonNull;

public enum DrawState {
    Normal, PartialPlayed, Played, Closed;

    public static DrawState valueOfCaseInsensitive(@NonNull String search){
        for (DrawState each : values()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        throw new IllegalArgumentException("can't parse value: " + search);
    }
}
