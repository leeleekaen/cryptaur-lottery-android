package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;

import java.math.BigInteger;

public class Ticket {

    public final Lottery lottery;
    public final int drawIndex;
    public final Instant drawDate;
    public final int index;
    public final int winLevel;
    public final BigInteger winAmount;
    public final BigInteger price;
    public final int[] numbers;
    public final int[] drawNumbers;

    public Ticket(Lottery lottery, JSONObject source) throws JSONException {
        this.lottery = lottery;
        JSONObjectHelper helper = new JSONObjectHelper(source);
        drawIndex = source.getInt("drawIndex");
        drawDate = helper.getInstant("drawDate");
        index = source.getInt("index");
        winLevel = source.getInt("winLevel");
        winAmount = helper.getUnsignedBigInteger("winAmount");
        price = helper.getUnsignedBigInteger("price");
        numbers = helper.getIntArray("numbers");
        drawNumbers = helper.getIntArray("drawNumbers");
    }
}
