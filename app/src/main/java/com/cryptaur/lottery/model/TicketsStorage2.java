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

    private final TreeSet<Ticket> playedTickets = new TreeSet<>(Ticket.SortComparator.INSTANCE);
    private final TreeSet<Ticket> activeTickets = new TreeSet<>(Ticket.SortComparator.INSTANCE);

    private final LotteryTicketsQueueJoiner joiner;
    private final int latestDraws[] = new int[Lottery.values().length];
    private final List<Ticket> ticketsToUpdate = new ArrayList<>();

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

    private void update(Ticket ticket) {

        if (ticket.isPlayed()) {
            activeTickets.remove(ticket);
            int indexUpdate = indexOfTicket(ticketsToUpdate, ticket, false);
            if (indexUpdate >= 0)
                ticketsToUpdate.remove(indexUpdate);
            playedTickets.add(ticket);
            if (maxPlayedDraws[ticket.lottery.ordinal()] < ticket.drawIndex)
                maxPlayedDraws[ticket.lottery.ordinal()] = ticket.drawIndex;
        } else {
            activeTickets.add(ticket);
        }
    }

    private int indexOfTicket(List<Ticket> tikets, Ticket ticket, boolean listIsOrdered) {
        Lottery lottery = ticket.lottery;
        for (int i = 0; i < tikets.size(); i++) {
            Ticket check = tikets.get(i);
            if (check.lottery == lottery) {
                if (check.index == ticket.index)
                    return i;
            }
            if (listIsOrdered && check.drawIndex < ticket.drawIndex)
                return -1;
        }
        return -1;
    }

    private Ticket oldTicket(Collection<Ticket> tickets, Ticket ticket, boolean listIsOrdered) {
        Lottery lottery = ticket.lottery;
        for (Ticket check : tickets) {
            if (check.lottery == lottery) {
                if (check.index == ticket.index) {
                    return check;
                }
                if (listIsOrdered && check.drawIndex < ticket.drawIndex)
                    return null;
            }

        }

        return null;
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
        ticketsToUpdate.clear();
        joiner.reset(values);
    }

    private void updateTicketPlayed(Ticket ticket) {
        int currentDraw = latestDraws[ticket.lottery.ordinal()];
        if (currentDraw > 0)
            ticket.setPlayed(ticket.drawIndex < currentDraw);
    }

    @Override
    public void onRequestResult(CurrentDraws responce) {
        for (Draw draw : responce.draws) {
            latestDraws[draw.lottery.ordinal()] = draw.number;
        }
        for (Ticket activeTicket : activeTickets) {
            updateTicketPlayed(activeTicket);
            if (activeTicket.isPlayed()) {
                ticketsToUpdate.add(activeTicket);
            }
        }
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
