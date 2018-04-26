package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;

import java.math.BigInteger;
import java.util.Comparator;
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

    private boolean isPlayed;

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

    public Ticket(JSONObject source) throws JSONException {
        this(Lottery.ofServerId(source.getInt("lotteryId")), source);
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

    public static Ticket buyTicket(Draw currentDraw, List<Integer> numbers) {
        int[] numbersArray = new int[numbers.size()];
        for (int i = 0; i < numbersArray.length; i++) {
            numbersArray[i] = numbers.get(i);
        }
        return new Ticket(currentDraw.lottery, currentDraw.number, currentDraw.startTime, -1, -1, null,
                currentDraw.getTicketPrice().amount, numbersArray);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject res = new JSONObject();
        res
                .put("drawIndex", drawIndex)
                .put("ticketIndex", index)
                .put("winLevel", winLevel)
                .put("lotteryId", lottery.getServerId());
        JSONObjectHelper helper = new JSONObjectHelper(res);
        helper
                .put("drawDate", drawDate)
                .put("winAmount", winAmount)
                .put("price", price)
                .put("numbers", numbers);
        return helper.getJson();
    }

    public boolean isWinNumber(int number) {
        return number < 0;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public static class SortComparator implements Comparator<Ticket> {
        public static final SortComparator INSTANCE = new SortComparator();

        @Override
        public int compare(Ticket o1, Ticket o2) {
            int result = o2.drawDate.compareTo(o1.drawDate);
            if (result != 0)
                return result;
            if (o1.lottery != o2.lottery)
                return o2.lottery.ordinal() - o1.lottery.ordinal();
            return o2.index - o1.index;
        }
    }
}
