package com.cryptaur.lottery.model;

import android.content.Context;

import com.cryptaur.lottery.mytickets.LastCheckedTicketIdsKeeper;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TicketsStorage2 implements ITicketStorageRead, GetObjectCallback<CurrentDraws> {

    private final ArrayList<Ticket> playedTickets = new ArrayList<>();
    private final ArrayList<Ticket> activeTickets = new ArrayList<>();

    private final LotteryTicketsQueueJoiner joiner;
    private final int latestDraws[];
    private final List<Ticket> ticketsToUpdate = new ArrayList<>();

    private final int[] maxIndex = new int[Lottery.values().length];
    private final int[] maxPlayedIndex = new int[Lottery.values().length];

    private int[] maxPlayedShownIndex;


    public TicketsStorage2(Lottery[] lotteries) {
        latestDraws = new int[Lottery.values().length];
        joiner = new LotteryTicketsQueueJoiner(lotteries);
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
        if (isUpdateRequest && tickets.startFrom == 0 && tickets.tickets.size() > 0) {
            Lottery lottery = tickets.lottery;
            maxIndex[lottery.ordinal()] = tickets.tickets.get(0).index;
            for (Ticket ticket : tickets.tickets) {
                updateTicketPlayed(ticket);
                update(ticket);
            }
        }
        joiner.add(tickets);

        Ticket ticket;
        while ((ticket = joiner.next()) != null) {
            updateTicketPlayed(ticket);
            if (ticket.isPlayed()) {
                playedTickets.add(ticket);
                if (maxPlayedIndex[ticket.lottery.ordinal()] < ticket.index) {
                    maxPlayedIndex[ticket.lottery.ordinal()] = ticket.index;
                }
            } else
                activeTickets.add(ticket);
        }
        Collections.sort(playedTickets, TicketSortComparator.INSTANCE);
        Collections.sort(activeTickets, TicketSortComparator.INSTANCE);
    }

    private void update(Ticket ticket) {
        Ticket oldActiveTicket = oldTicket(activeTickets, ticket, true);
        if (ticket.isPlayed()) {
            if (oldActiveTicket != null) {
                activeTickets.remove(oldActiveTicket);
                int indexUpdate = indexOfTicket(ticketsToUpdate, ticket, false);
                if (indexUpdate >= 0)
                    ticketsToUpdate.remove(indexUpdate);
                playedTickets.add(ticket);
            } else {
                Ticket oldPlayedTicket = oldTicket(playedTickets, ticket, true);
                if (oldPlayedTicket == null) {
                    playedTickets.add(ticket);
                }
            }
            if (maxPlayedIndex[ticket.lottery.ordinal()] < ticket.index) {
                maxPlayedIndex[ticket.lottery.ordinal()] = ticket.index;
            }
        } else {
            if (oldActiveTicket == null)
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
                result.add(new TicketsToLoad(lottery, 0, maxIndex[lottery.ordinal()] - smallestId + 10));
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

    @Override
    public void onShowPlayedTicketIds(Context context) {
        LastCheckedTicketIdsKeeper.updateLastCheckedPlayedTicketIds(context, maxPlayedIndex);
        maxPlayedShownIndex = Arrays.copyOf(maxPlayedIndex, maxPlayedIndex.length);
    }

    @Override
    public int getUnshownTicketsAmount(Context context) {
        if (maxPlayedShownIndex == null) {
            maxPlayedShownIndex = LastCheckedTicketIdsKeeper.getLastCheckedPlayedTicketIds(context);
        }
        int sum = 0;
        for (int i = 0; i < maxPlayedIndex.length; i++) {
            if (maxPlayedIndex[i] > 0)
                sum += maxPlayedIndex[i] - maxPlayedShownIndex[i];
        }

        return sum;
    }

    private static class TicketSortComparator implements Comparator<Ticket> {
        public static final TicketSortComparator INSTANCE = new TicketSortComparator();

        @Override
        public int compare(Ticket o1, Ticket o2) {
            return o2.drawDate.compareTo(o1.drawDate);
        }

    }
}
