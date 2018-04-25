package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsToLoad;
import com.cryptaur.lottery.transport.model.TicketsType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class TicketsStorage2 implements ITicketStorageRead, GetObjectCallback<CurrentDraws> {

    private final TreeSet<Ticket> activeTickets = new TreeSet<>(Ticket.SortComparator.INSTANCE);
    private final TreeSet<Ticket> playedTickets = new TreeSet<>(Ticket.SortComparator.INSTANCE);
    private final TreeSet<Ticket> ticketsToUpdate = new TreeSet<>(Ticket.SortComparator.INSTANCE);

    private final LotteryTicketsQueueJoiner joiner;
    private final int latestDraws[] = new int[Lottery.values().length];

    private final int[] maxPlayedDraws = new int[Lottery.values().length];
    private final int[] minLoadedDraws = new int[Lottery.values().length];

    public TicketsStorage2(Lottery[] lotteries) {
        joiner = new LotteryTicketsQueueJoiner(lotteries);
        Arrays.fill(minLoadedDraws, Integer.MAX_VALUE);
    }

    @Override
    public Collection<Ticket> getTickets(TicketsType type) {
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

    /**
     * receives LotteryTicketsList (server reply).
     * if isUpdateRequest is true then updates all tickets (they are expected but not required to be in active/played ticket sets)
     * else puts the LotteryTicketsList into the joiner, takes all Tickets from the joiner (until null) and puts them into active/played ticket sets
     *
     * @param tickets
     * @param isUpdateRequest
     */
    public void add(LotteryTicketsList tickets, boolean isUpdateRequest) {
        if (isUpdateRequest && tickets.tickets.size() > 0) {
            for (Ticket ticket : tickets.tickets) {
                updateTicketPlayed(ticket);
                update(ticket);
            }
        } else {
            joiner.add(tickets);

            Ticket ticket;
            while ((ticket = joiner.next()) != null) {
                updateTicketPlayed(ticket);
                if (ticket.isPlayed()) {
                    playedTickets.add(ticket);
                    if (maxPlayedDraws[ticket.lottery.ordinal()] < ticket.drawIndex)
                        maxPlayedDraws[ticket.lottery.ordinal()] = ticket.drawIndex;
                } else
                    activeTickets.add(ticket);
            }
        }
    }

    /**
     * updates ticket that might exist in active or played tickets list
     *
     * @param ticket
     */
    private void update(Ticket ticket) {
        if (ticket.isPlayed()) {
            activeTickets.remove(ticket);
            ticketsToUpdate.remove(ticket);
            playedTickets.add(ticket);
            if (maxPlayedDraws[ticket.lottery.ordinal()] < ticket.drawIndex)
                maxPlayedDraws[ticket.lottery.ordinal()] = ticket.drawIndex;
        } else {
            activeTickets.add(ticket);
        }
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
        ticketsToUpdate.clear();
        joiner.reset(values);
    }

    /**
     * update "isPlayed" flag for the ticket
     * @param ticket
     */
    private void updateTicketPlayed(Ticket ticket) {
        int currentDraw = latestDraws[ticket.lottery.ordinal()];
        if (currentDraw > 0)
            ticket.setPlayed(ticket.drawIndex < currentDraw);
    }

    @Override
    public void onRequestResult(CurrentDraws draws) {
        for (Draw draw : draws.draws) {
            latestDraws[draw.lottery.ordinal()] = draw.number;
        }
        for (Ticket activeTicket : activeTickets) {
            updateTicketPlayed(activeTicket);
            if (activeTicket.isPlayed()) {
                ticketsToUpdate.add(activeTicket);
            }
        }
    }

    public List<TicketsToLoad> getTicketsToLoad() {
        return joiner.getTicketsToLoad();
    }

    public List<TicketsToLoad> getTicketsToUpdate() {
        if (ticketsToUpdate == null)
            return null;

        List<TicketsToLoad> result = new ArrayList<>();

        for (Lottery lottery : Lottery.values()) {
            int amount = 0;
            int smallestId = Integer.MAX_VALUE;
            for (Ticket ticket : ticketsToUpdate) {
                if (ticket.lottery == lottery) {
                    ++amount;
                    if (ticket.index < smallestId)
                        smallestId = ticket.index;
                }
            }
            if (amount > 0) {
                result.add(new TicketsToLoad(lottery, 0, amount + 10));
            }
        }
        return result;
    }

    @Override
    public void onNetworkRequestError(Exception e) {
    }

    @Override
    public void onCancel() {
    }
}
