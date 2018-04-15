package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsList;
import com.cryptaur.lottery.transport.model.TicketsType;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketsStorage implements ITicketStorageRead {
    private static final long MAX_LOTTERY_DURATION = 86400_000;// 1 day
    private final List<Ticket> playedTickets = new ArrayList<>();
    private final List<Ticket> activeTickets = new ArrayList<>();
    private boolean canLoadMorePlayedTickets = true;
    private boolean canLoadMoreActiveTickets = true;

    @Override
    public List<Ticket> getTickets(TicketsType type) {
        switch (type) {
            case Active:
                return Collections.unmodifiableList(activeTickets);

            case Played:
                return Collections.unmodifiableList(playedTickets);
        }
        throw new IllegalArgumentException("not implemented for type: " + type.name());
    }

    @Override
    public boolean canLoadMoreTickets(TicketsType type) {
        switch (type) {
            case Active:
                return canLoadMoreActiveTickets;

            case Played:
                return canLoadMorePlayedTickets;
        }
        throw new IllegalArgumentException("not implemented for type: " + type.name());
    }

    public void addTickets(TicketsList tickets) {
        if (tickets.tickets.length == 0) {
            canLoadMorePlayedTickets = canLoadMoreActiveTickets = false;
            return;
        }

        Instant now = Instant.now();
        for (Ticket ticket : tickets.tickets) {
            if (ticket.isPlayed()) {
                playedTickets.add(ticket);

                if (canLoadMoreActiveTickets
                        && ticket.drawDate.until(now, ChronoUnit.SECONDS) > MAX_LOTTERY_DURATION) {
                    // if this ticket is played more then a day before than is is boutht more then a day before
                    // and, thus, there will be no more active tickets
                    canLoadMoreActiveTickets = false;
                }
            } else {
                activeTickets.add(ticket);
            }
            if (ticket.index <= 0) {
                canLoadMorePlayedTickets = canLoadMoreActiveTickets = false;
            }
        }
    }

    public int getTotalTickets() {
        return playedTickets.size() + activeTickets.size();
    }

    public void clear() {
        playedTickets.clear();
        activeTickets.clear();
        canLoadMorePlayedTickets = canLoadMoreActiveTickets = true;
    }
}
