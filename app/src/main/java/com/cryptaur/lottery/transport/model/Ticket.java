package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;

import java.math.BigInteger;
import java.util.List;

public class Ticket {

    public final Lottery lottery;
    public final int drawIndex;
    public final Instant drawDate;
    public final int index;
    public final int winLevel;
    public final BigInteger winAmount;
    public final BigInteger price;
    public final int[] numbers;

    public Ticket(Lottery lottery, JSONObject source) throws JSONException {
        this.lottery = lottery;
        JSONObjectHelper helper = new JSONObjectHelper(source);
        drawIndex = source.getInt("drawIndex");
        drawDate = helper.getInstant("drawDate");
        index = source.getInt("ticketIndex");
        winLevel = source.getInt("winLevel");
        winAmount = helper.getUnsignedBigInteger("winAmount");
        price = helper.getUnsignedBigInteger("price");
        numbers = helper.getIntArray("numbers");
    }

    public Ticket(Lottery lottery, int drawIndex, Instant drawDate, int index, int winLevel, BigInteger winAmount, BigInteger price, int[] numbers) {
        this.lottery = lottery;
        this.drawIndex = drawIndex;
        this.drawDate = drawDate;
        this.index = index;
        this.winLevel = winLevel;
        this.winAmount = winAmount;
        this.price = price;
        this.numbers = numbers;
    }

    public static Ticket createTemp(int index) {
        Lottery lottery = Lottery.values()[(int) (Math.random() * 3)];
        int[] numbers = new int[lottery.getNumbersAmount()];
        int maxVal = lottery.getMaxValue();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = (int) (Math.random() * maxVal) + 1;
            if (Math.random() > 0.8) {
                numbers[i] = -numbers[i];
            }
        }

        boolean isPlayed = Math.random() > 0.5;
        long timestamp = (long) (isPlayed ?
                -Math.random() * 86400_000 * 3 :
                Math.random() * 68400_000);
        timestamp += System.currentTimeMillis();

        return new Ticket(
                lottery,
                (int) (Math.random() * 10),
                Instant.ofEpochMilli(timestamp),
                index,
                isPlayed ? 2 : -1,
                BigInteger.TEN,
                BigInteger.TEN,
                numbers
        );
    }

    public static Ticket buyTicket(Draw currentDraw, List<Integer> numbers) {
        int[] numbersArray = new int[numbers.size()];
        for (int i = 0; i < numbersArray.length; i++) {
            numbersArray[i] = numbers.get(i);
        }
        return new Ticket(currentDraw.lottery, currentDraw.number, currentDraw.startTime, -1, -1, null,
                currentDraw.getTicketPrice().amount, numbersArray);
    }

    public boolean isWinNumber(int number) {
        return number < 0;
    }

    public boolean isPlayed() {
        return winLevel >= 0;
    }
}
