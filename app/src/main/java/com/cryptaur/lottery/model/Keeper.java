package com.cryptaur.lottery.model;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsList;

import java.math.BigInteger;

public class Keeper {
    private static final long DRAWS_UPDATE_TIMEOUT = 600_000; // 10 mins

    private static volatile Keeper keeper;

    private final RequestJoiner<CurrentDraws> currentDraws = new RequestJoiner<>();
    private final TicketsRequestJoiner ticketRequestJoiner = new TicketsRequestJoiner();

    public Keeper(Context context) {

    }

    public static Keeper getInstance(Context context) {
        Keeper local = keeper;
        if (local == null) {
            synchronized (Keeper.class) {
                local = keeper;
                if (local == null) {
                    local = keeper = new Keeper(context);
                }
            }
        }
        return local;
    }

    public int getUncheckedTicketsAmount() {
        return 1;
    }

    @UiThread
    public void getCurrentDraws(final GetObjectCallback<CurrentDraws> listener) {
        if (!currentDraws.isExecutingRequest() && currentDraws.isResultOutdated(DRAWS_UPDATE_TIMEOUT)) {
            currentDraws.setExecutingRequest(true);
            Transport.INSTANCE.getLotteries(currentDraws);
        }

        if (currentDraws.isExecutingRequest()) {
            currentDraws.addCallback(listener);
        }

        CurrentDraws draws = currentDraws.getValue();
        if (draws != null) {
            listener.onRequestResult(draws);
        }
    }

    public ITicketStorageRead getTicketsStorage() {
        return ticketRequestJoiner.getTicketsStorage();
    }

    public void updateTickets(final GetObjectCallback<ITicketStorageRead> listener) {
        ticketRequestJoiner.addCallback(listener);
        if (!ticketRequestJoiner.isExecutingRequest()) {
            int maxIndex = 55;
            Ticket[] tickets = new Ticket[10];
            int totalTickets = ticketRequestJoiner.getTicketsStorage().getTotalTickets();
            int indexStart = maxIndex - totalTickets - 1;
            for (int i = 0; i < tickets.length; i++) {
                tickets[i] = Ticket.createTemp(indexStart - i);
            }
            TicketsList list = new TicketsList(totalTickets, tickets);

            new Handler().postDelayed(() -> ticketRequestJoiner.onNetworkRequestDone(null, list), 2000);
        }
    }

    public void refreshTickets() {
        ticketRequestJoiner.getTicketsStorage().clear();
    }

    public void getUnusedWin(final GetObjectCallback<BigInteger> listener) {
        listener.onRequestResult(BigInteger.valueOf(100_0000_0000L));
    }

}
