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
    public final long timestamp;
    public final int[] numbers;
    private Money ticketPrice;

    public Draw(Lottery lottery, JSONObject source) throws JSONException {
        this.lottery = lottery;
        timestamp = System.currentTimeMillis();
        JSONObjectHelper helper = new JSONObjectHelper(source);
        number = source.getInt("number");
        startTime = helper.getInstant("date");
        lotteryState = LotteryState.valueOfCaseInsensitive(helper.getStringNullable("state"));

        drawState = DrawState.valueOfCaseInsensitive(source.getString("drawState"));
        jackpot = helper.getUnsignedBigInteger("jackpot");
        reserve = helper.getUnsignedBigInteger("reserve");
        jackpotAdded = helper.getUnsignedBigInteger("jackpotAdded");
        reserveAdded = helper.getUnsignedBigInteger("reserveAdded");

        ticketsBought = helper.getUnsignedBigInteger("ticketPrice").intValue();
        numbers = helper.getIntArray("numbers");

        BigInteger ticketPrice = helper.getUnsignedBigInteger("ticketPrice");
        BigInteger fee = helper.getUnsignedBigInteger("buyTicketGasFee");
        this.ticketPrice = new Money(ticketPrice, fee);
    }

    public Draw(JSONObject source) throws JSONException {
        this(Lottery.ofServerId(source.getInt("lotteryId")), source);
    }

    public Money getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Money ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
