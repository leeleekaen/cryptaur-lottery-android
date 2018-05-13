package com.cryptaur.lottery.model;

public class MinMax {
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public void add(int value) {
        if (min > value)
            min = value;
        if (max < value)
            max = value;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public void reset() {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
    }
}
