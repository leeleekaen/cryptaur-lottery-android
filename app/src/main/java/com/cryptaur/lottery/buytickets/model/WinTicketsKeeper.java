package com.cryptaur.lottery.buytickets.model;

import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.WinTicket;
import com.cryptaur.lottery.transport.model.WinTicketReply;
import com.cryptaur.lottery.transport.model.WinTicketsRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class WinTicketsKeeper {
    private final TreeSet<WinTicket> tickets = new TreeSet<>(new TicketsComparator());

    private final Draw draw;

    private boolean canLoadMore = true;

    public WinTicketsKeeper(Draw draw) {
        this.draw = draw;
    }

    public void add(WinTicketReply tickets) {
        this.tickets.addAll(tickets.tickets);
        if (tickets.tickets.size() == 0 || tickets.tickets.size() < tickets.request.amount)
            canLoadMore = false;
    }

    public Collection<WinTicket> getTickets() {
        return Collections.unmodifiableCollection(tickets);
    }

    public WinTicketsRequest nextRequest() {
        return new WinTicketsRequest(tickets.size(), 20, draw);
    }

    public boolean canLoadMore() {
        return canLoadMore;
    }

    public void reset() {
        tickets.clear();
        canLoadMore = true;
    }

    private static class TicketsComparator implements Comparator<WinTicket> {
        @Override
        public int compare(WinTicket o1, WinTicket o2) {
            return o2.index - o1.index;
        }
    }
}