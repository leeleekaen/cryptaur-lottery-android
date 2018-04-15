package com.cryptaur.lottery.transport.model;

public class TicketsList {
    public final Ticket[] tickets;
    int startFrom;

    public TicketsList(int startFrom, Ticket[] tickets) {
        this.tickets = tickets;
        this.startFrom = startFrom;
    }
}
