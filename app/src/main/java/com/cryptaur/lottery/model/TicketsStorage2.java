package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsToLoad;
import com.cryptaur.lottery.transport.model.TicketsType;

import java.util.ArrayList;
import java.util.List;

public class TicketsStorage2 implements ITicketStorageRead {

    private final List<Ticket> playedTickets = new ArrayList<>();
    private final List<Ticket> activeTickets = new ArrayList<>();

    private final LotteryTicketsQueueJoiner joiner;

    public TicketsStorage2(Lottery[] lotteries) {
        joiner = new LotteryTicketsQueueJoiner(lotteries);
    }

    @Override
    public List<Ticket> getTickets(TicketsType type) {
        switch (type) {
            case Played:
                return playedTickets;
            case Active:
                return activeTickets;
        }
        throw new RuntimeException("Not implemented for: " + type.name());
    }

    @Override
    public boolean canLoadMoreTickets(TicketsType type) {
        switch (type) {
            case Active:
                return playedTickets.size() == 0 && joiner.canLoadMoreTickets();
            case Played:
                return joiner.canLoadMoreTickets();
        }

        return false;
    }

    public void add(LotteryTicketsList ticketsList) {
        joiner.add(ticketsList);
        Ticket ticket;
        while ((ticket = joiner.next()) != null) {
            if (ticket.isPlayed())
                playedTickets.add(ticket);
            else
                activeTickets.add(ticket);
        }
    }

    public List<TicketsToLoad> getTicketsToLoad() {
        return joiner.getTicketsToLoad();
    }

    public boolean checkCanReturnRequest(TicketsType type, int minAmount) {
        if (!joiner.canLoadMoreTickets()) {
            return true;
        }
        switch (type) {
            case Active:
                return playedTickets.size() > 0 || activeTickets.size() >= minAmount;
            case Played:
                return playedTickets.size() >= minAmount;
        }
        throw new RuntimeException("Not implemented for type: " + type.name());
    }

    public void reset(Lottery[] values) {
        playedTickets.clear();
        activeTickets.clear();
        joiner.reset(values);
    }
}
