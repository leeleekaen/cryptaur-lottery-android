package com.cryptaur.lottery.transport.model;


import org.threeten.bp.Instant;

public class BuyTicketTransactionObject {
    public final Lottery lottery;
    public final Instant drawDate;
    public final int drawIndex;
    public final int[] numbers;
    public final long timestamp;

    public BuyTicketTransactionObject(Ticket ticket) {
        lottery = ticket.lottery;
        drawDate = ticket.drawDate;
        drawIndex = ticket.drawIndex;
        numbers = ticket.numbers;
        timestamp = System.currentTimeMillis();
    }
}
