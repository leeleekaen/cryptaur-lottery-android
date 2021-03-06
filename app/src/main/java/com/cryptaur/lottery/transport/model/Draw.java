package com.cryptaur.lottery.transport.model;


import android.support.annotation.NonNull;

import com.cryptaur.lottery.Const;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.math.BigInteger;

public class Draw extends DrawId {
    public final Instant startTime;
    public final LotteryState lotteryState;
    public final DrawState drawState;
    public final BigInteger jackpot;
    public final BigInteger reserve;
    public final BigInteger jackpotAdded;
    public final BigInteger reserveAdded;
    public final BigInteger paid;
    public final int ticketsBought;
    public final long timestamp;
    public final int[] numbers;
    @NonNull
    private Money ticketPrice;

    public Draw(Lottery lottery, JSONObject source) throws JSONException {
        super(lottery, source.getInt("number"));
        timestamp = System.currentTimeMillis();
        JSONObjectHelper helper = new JSONObjectHelper(source);

        startTime = helper.getInstant("date");
        lotteryState = LotteryState.valueOfCaseInsensitive(helper.getStringNullable("state"));

        drawState = DrawState.valueOfCaseInsensitive(source.getString("drawState"));
        jackpot = helper.getUnsignedBigInteger("jackpot");
        reserve = helper.getUnsignedBigInteger("reserve");
        jackpotAdded = helper.getUnsignedBigInteger("jackpotAdded");
        reserveAdded = helper.getUnsignedBigInteger("reserveAdded");
        paid = helper.getUnsignedBigInteger("payed");

        ticketsBought = source.getInt("ticketsBought");
        numbers = helper.getIntArray("numbers");

        BigInteger ticketPrice = helper.getUnsignedBigInteger("ticketPrice");
        BigInteger fee = helper.getUnsignedBigInteger("buyTicketGasFee");
        this.ticketPrice = new Money(ticketPrice, fee);
    }

    public Draw(JSONObject source) throws JSONException {
        this(Lottery.ofServerId(source.getInt("lotteryId")), source);
    }

    @NonNull
    public Money getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(@NonNull Money ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public boolean isPlayed() {
        return numbers != null && numbers.length >= lottery.getNumbersAmount();
    }

    public BigInteger getCollected() {
        return ticketPrice.amount.multiply(BigInteger.valueOf(ticketsBought));
    }

    public long secondsToDraw() {
        return Instant.now().until(startTime, ChronoUnit.SECONDS);
    }

    public boolean canBuyTicket() {
        return secondsToDraw() > Const.STOP_TICKET_SELL_INTERVAL_SEC;
    }
}
