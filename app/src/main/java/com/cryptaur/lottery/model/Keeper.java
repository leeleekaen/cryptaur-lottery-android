package com.cryptaur.lottery.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.TicketsType;

import java.math.BigInteger;

public class Keeper {
    private static final long DRAWS_UPDATE_TIMEOUT = 600_000; // 10 mins
    private static final long BALANCE_UPDATE_TIMEOUT = 600_000; // 10 mins
    private static final long WIN_AMOUNT_UPDATE_TIMEOUT = 600_000; // 10 mins

    private static volatile Keeper keeper;

    private final SimpleItemKeeper<CurrentDraws> currentDrawsKeeper
            = new SimpleItemKeeper<>(DRAWS_UPDATE_TIMEOUT, Transport.INSTANCE::getLotteries);
    private final SimpleItemKeeper<BigInteger> balanceKeeper;
    private final SimpleItemKeeper<Money> winAmountKeeper = new SimpleItemKeeper<>(WIN_AMOUNT_UPDATE_TIMEOUT, Transport.INSTANCE::getWinAmount);

    private final TicketsRequestJoiner ticketRequestJoiner;

    private Keeper(Context context) {
        final Context ctx = context.getApplicationContext();
        balanceKeeper = new SimpleItemKeeper<>(BALANCE_UPDATE_TIMEOUT, listener -> Transport.INSTANCE.getBalance(ctx, listener));
        ticketRequestJoiner = new TicketsRequestJoiner(this);
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
    public void getCurrentDraws(final GetObjectCallback<CurrentDraws> listener, boolean force) {
        currentDrawsKeeper.requestValue(listener, force);

        CurrentDraws draws = currentDrawsKeeper.getValue();
        if (draws != null) {
            listener.onRequestResult(draws);
        }
    }

    public void getBalance(GetObjectCallback<BigInteger> listener, boolean force) {
        balanceKeeper.requestValue(listener, force);
    }

    public ITicketStorageRead getTicketsStorage() {
        return ticketRequestJoiner.getTicketsStorage();
    }

    public void updateTickets(TicketsType type, int minAmount, final GetObjectCallback<ITicketStorageRead> listener) {
        ticketRequestJoiner.requestTicketStorage(type, minAmount, listener);
    }

    public void refreshTickets() {
        ticketRequestJoiner.reset();
    }

    public void updateTicketFee(Draw currentDraw, @Nullable GetObjectCallback<Money> listener) {
        Transport.INSTANCE.getTicketFee(currentDraw.lottery, new TicketFeeUpdater(currentDraw, listener));
    }

    public void getWinAmount(GetObjectCallback<Money> listener, boolean forceUpdate) {
        winAmountKeeper.requestValue(listener, forceUpdate);
    }

    public void addCurrentDrawsListener(GetObjectCallback<CurrentDraws> listener) {
        currentDrawsKeeper.addListener(listener);
    }

    public void removeCurrentDrawsListener(GetObjectCallback<CurrentDraws> listener) {
        currentDrawsKeeper.removeListener(listener);
    }

    public void addTicketsListener(GetObjectCallback<ITicketStorageRead> listener) {
        ticketRequestJoiner.addListener(listener);
    }

    public void removeTicketsListener(GetObjectCallback<ITicketStorageRead> listener) {
        ticketRequestJoiner.removeListener(listener);
    }
}
