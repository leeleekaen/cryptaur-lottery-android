package com.cryptaur.lottery.transport.model;


import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;

import java.math.BigInteger;

public class Draw {
    public final Lottery lottery;
    public final int number;
    public final Instant startTime;
    public final LotteryState lotteryState;
    public final DrawState drawState;
    public final BigInteger jackpot;
    public final BigInteger reserve;
    public final BigInteger jackpotAdded;
    public final BigInteger reserveAdded;
    public final int ticketsBought;
    public final BigInteger ticketPrice;
    public final int[] numbers;

    public Draw(Lottery lottery, JSONObject source) throws JSONException {
        this.lottery = lottery;
        JSONObjectHelper helper = new JSONObjectHelper(source);
        number = source.getInt("number");
        startTime = helper.getInstant("date");
        lotteryState = LotteryState.valueOfCaseInsensitive(helper.getStringNullable("state"));

        drawState = DrawState.valueOfCaseInsensitive(source.getString("drawState"));
        jackpot = helper.getUnsignedBigInteger("jackpot");
        reserve = helper.getUnsignedBigInteger("reserve");
        jackpotAdded = helper.getUnsignedBigInteger("jackpotAdded");
        reserveAdded = helper.getUnsignedBigInteger("reserveAdded");
        ticketPrice = helper.getUnsignedBigInteger("ticketPrice");
        ticketsBought = helper.getUnsignedBigInteger("ticketPrice").intValue();
        //ticketsBought = source.getInt("ticketsBought");

        numbers = helper.getIntArray("numbers");
    }

    public Draw(JSONObject source) throws JSONException {
        this(Lottery.ofServerId(source.getInt("lotteryId")), source);
    }
}
