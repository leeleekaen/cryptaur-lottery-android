package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.Ticket;

import java.util.PriorityQueue;

public class LotteryTicketsQueue {
    public final Lottery lottery;
    private final PriorityQueue<Ticket> tickets = new PriorityQueue<>(50, Ticket.SortComparator.INSTANCE);

    private int nextLoadIndex = 0;
    private boolean canLoadMore = true;

    public LotteryTicketsQueue(Lottery lottery) {
        this.lottery = lottery;
    }

    public void add(LotteryTicketsList ticketsList) {
        if (ticketsList.tickets.size() == 0) {
            canLoadMore = false;
        } else {
            tickets.addAll(ticketsList.tickets);
            nextLoadIndex = Math.max(nextLoadIndex, ticketsList.startFrom + ticketsList.tickets.size());
        }
    }

    public Ticket peek() {
        return tickets.peek();
    }

    public Ticket poll() {
        return tickets.poll();
    }

    public boolean canLoadMode() {
        return canLoadMore;
    }

    public int getNextLoadStart() {
        return nextLoadIndex;
    }

    public boolean shouldLoad() {
        return tickets.size() == 0 && canLoadMode();
    }

    public int size() {
        return tickets.size();
    }
}
