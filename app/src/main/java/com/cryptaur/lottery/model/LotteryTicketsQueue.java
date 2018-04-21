package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.Ticket;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class LotteryTicketsQueue {
    public final Lottery lottery;
    private final Deque<Ticket> tickets = new ArrayDeque<>();
    private int lastElementIndex = Integer.MAX_VALUE;
    private int totalAddedTickets = 0;
    private int skippedValues = 0;

    public LotteryTicketsQueue(Lottery lottery) {
        this.lottery = lottery;
    }

    public void add(List<Ticket> list) {
        if (list.size() == 0) {
            lastElementIndex = 0;
            return;
        }

        while (list.size() > 0 && list.get(0).index >= lastElementIndex) {
            list.remove(0);
            ++skippedValues;
        }

        totalAddedTickets += list.size();

        tickets.addAll(list);
        if (tickets.size() > 0) {
            lastElementIndex = tickets.getLast().index;
        }
    }

    public Ticket peek() {
        return tickets.peek();
    }

    public Ticket poll() {
        return tickets.poll();
    }

    public boolean canLoadMode() {
        return lastElementIndex > 0;
    }

    public int getNextLoadStart() {
        return totalAddedTickets + skippedValues;
    }

    public boolean shouldLoad() {
        return tickets.size() == 0 && canLoadMode();
    }

    public int size() {
        return tickets.size();
    }
}
