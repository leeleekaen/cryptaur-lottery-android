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
    public static final long DRAWS_UPDATE_TIMEOUT = 600_000; // 10 mins
    public static final long BALANCE_UPDATE_TIMEOUT = 600_000; // 10 mins
    public static final long WIN_AMOUNT_UPDATE_TIMEOUT = 600_000; // 10 mins

    private static volatile Keeper keeper;

    public final CurrentDrawsKeeper currentDrawsKeeper = new CurrentDrawsKeeper();
    private final SimpleItemKeeper<BigInteger> balanceKeeper = new SimpleItemKeeper<>(BALANCE_UPDATE_TIMEOUT, Transport.INSTANCE::getBalance);
    private final SimpleItemKeeper<Money> winAmountKeeper = new SimpleItemKeeper<>(WIN_AMOUNT_UPDATE_TIMEOUT, Transport.INSTANCE::getWinAmount);

    private final TicketsKeeper ticketsKeeper = new TicketsKeeper(this);
    public final DrawTicketsKeeper drawTicketsKeeper = new DrawTicketsKeeper(this, ticketsKeeper);

    private Keeper(Context context) {
        currentDrawsKeeper.addOnPlayedDrawsChangedListener((oldPlayedDrawIds, newPlayedDrawIds, currentDraws) -> {
            ticketsKeeper.reset();
            updateTickets(TicketsType.Played, 10, null);
        });
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

    @UiThread
    public void getCurrentDraws(final GetObjectCallback<CurrentDraws> listener, boolean force) {
        currentDrawsKeeper.requestValue(listener, force);

        CurrentDraws draws = currentDrawsKeeper.getValue();
        if (draws != null && !force) {
            listener.onRequestResult(draws);
        }
    }

    public void getBalance(GetObjectCallback<BigInteger> listener, boolean force) {
        balanceKeeper.requestValue(listener, force);
    }

    public ITicketStorageRead getTicketsStorage() {
        return ticketsKeeper.getTicketsStorage();
    }

    public void updateTickets(TicketsType type, int minAmount, final GetObjectCallback<ITicketStorageRead> listener) {
        ticketsKeeper.requestTicketStorage(type, minAmount, listener);
    }

    public void refreshTickets() {
        ticketsKeeper.reset();
    }

    public void updateTicketFee(Draw currentDraw, @Nullable GetObjectCallback<Money> listener) {
        Transport.INSTANCE.getTicketFee(currentDraw, new TicketFeeUpdater(currentDraw, listener));
    }

    public void getWinAmount(SimpleGetObjectCallback<Money> listener, boolean forceUpdate) {
        winAmountKeeper.requestValue(listener, forceUpdate);
    }

    public void addTicketsListener(SimpleGetObjectCallback<ITicketStorageRead> listener) {
        ticketsKeeper.addListener(listener);
    }

    public void removeTicketsListener(GetObjectCallback<ITicketStorageRead> listener) {
        ticketsKeeper.removeListener(listener);
    }

    public void clear() {
        currentDrawsKeeper.clear();
        balanceKeeper.clear();
        winAmountKeeper.clear();
        ticketsKeeper.reset();
    }
}
