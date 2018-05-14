package com.cryptaur.lottery.model;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.TicketsType;

import java.math.BigInteger;

import static com.cryptaur.lottery.Const.GET_TICKETS_STEP;

public class Keeper {
    static final long DRAWS_UPDATE_TIMEOUT = 600_000; // 10 mins
    private static final long BALANCE_UPDATE_TIMEOUT = 600_000; // 10 mins
    private static final long WIN_AMOUNT_UPDATE_TIMEOUT = 600_000; // 10 mins

    public static final Keeper INSTANCE = new Keeper();

    public final CurrentDrawsKeeper currentDrawsKeeper = new CurrentDrawsKeeper();
    private final SimpleItemKeeper<BigInteger> balanceKeeper = new SimpleItemKeeper<>(BALANCE_UPDATE_TIMEOUT, Transport.INSTANCE::getBalance);
    private final SimpleItemKeeper<Money> winAmountKeeper = new SimpleItemKeeper<>(WIN_AMOUNT_UPDATE_TIMEOUT, Transport.INSTANCE::getWinAmount);

    final TicketsKeeper ticketsKeeper = new TicketsKeeper(this);
    public final DrawTicketsKeeper drawTicketsKeeper = new DrawTicketsKeeper(this, ticketsKeeper);

    private Keeper() {
        currentDrawsKeeper.addOnPlayedDrawsChangedListener((oldPlayedDrawIds, newPlayedDrawIds, currentDraws) -> {
            refreshTickets(true);
            updateTickets(TicketsType.Played, GET_TICKETS_STEP, null);
        });
        SessionTransport.INSTANCE.addOnAddressChangedListener(newAddress -> {
            refreshTickets(true);
            if (newAddress != null)
                updateTickets(TicketsType.Played, GET_TICKETS_STEP, null);
        });
    }

    @UiThread
    public void getCurrentDraws(final GetObjectCallback<CurrentDraws> listener, boolean force) {
        currentDrawsKeeper.requestValue(listener, force);

        CurrentDraws draws = currentDrawsKeeper.getValue();
        if (draws != null && !force) {
            listener.onRequestResult(draws);
        }
    }

    public void getBalance(SimpleGetObjectCallback<BigInteger> listener, boolean force) {
        balanceKeeper.requestValue(listener, force);
    }

    public ITicketStorageRead getTicketsStorage() {
        return ticketsKeeper.getTicketsStorage();
    }

    public void updateTickets(TicketsType type, int minAmount, final GetObjectCallback<ITicketStorageRead> listener) {
        ticketsKeeper.requestTicketStorage(type, minAmount, listener);
    }

    public void refreshTickets(boolean refreshTransactions) {
        ticketsKeeper.reset();
        if (refreshTransactions) {
            TransactionKeeper.INSTANCE.doUpdateTransactions();
        }
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
        refreshTickets(true);
    }

    public void addBalanceListener(SimpleGetObjectCallback<BigInteger> listener) {
        balanceKeeper.addListener(listener);
    }

    public void removeBalanceListener(SimpleGetObjectCallback<BigInteger> listener) {
        balanceKeeper.removeListener(listener);
    }
}
