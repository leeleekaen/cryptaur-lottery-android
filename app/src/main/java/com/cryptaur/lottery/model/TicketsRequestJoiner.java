package com.cryptaur.lottery.model;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.TicketsToLoad;
import com.cryptaur.lottery.transport.model.TicketsType;

import java.util.ArrayList;
import java.util.List;

@UiThread
public class TicketsRequestJoiner implements NetworkRequest.NetworkRequestListener<LotteryTicketsList> {

    private final TicketsStorage2 ticketsStorage = new TicketsStorage2(Lottery.values());
    private final List<LotteryTicketDemand> lotteryTicketDemands = new ArrayList<>();
    private int executingRequests = 0;


    public TicketsRequestJoiner() {
    }

    public void requestTicketStorage(TicketsType type, int minAmount, final GetObjectCallback<ITicketStorageRead> listener) {
        if (executingRequests > 0) {
            lotteryTicketDemands.add(new LotteryTicketDemand(type, minAmount, listener));
            return;
        }

        if (ticketsStorage.checkCanReturnRequest(type, minAmount)) {
            listener.onRequestResult(ticketsStorage);
        } else {
            LotteryTicketDemand demand = new LotteryTicketDemand(type, minAmount, listener);
            lotteryTicketDemands.add(demand);
            if (!runRequests()) {
                listener.onRequestResult(ticketsStorage);
                lotteryTicketDemands.remove(demand);
            }
        }
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, LotteryTicketsList responce) {
        --executingRequests;
        ticketsStorage.add(responce);
        for (int i = 0; i < lotteryTicketDemands.size(); i++) {
            LotteryTicketDemand demand = lotteryTicketDemands.get(i);
            if (ticketsStorage.checkCanReturnRequest(demand.type, demand.minAmount)) {
                demand.listener.onRequestResult(ticketsStorage);
                lotteryTicketDemands.remove(i--);
            }
        }
        if (executingRequests == 0 && lotteryTicketDemands.size() > 0) {
            if (!runRequests()) {
                failAllDemands(new Exception("no requests run with existing demands"));
            }
        }
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        --executingRequests;
        if (executingRequests == 0)
            failAllDemands(e);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        --executingRequests;
        if (executingRequests == 0) {
            for (LotteryTicketDemand demand : lotteryTicketDemands) {
                demand.listener.onCancel();
            }
        }
    }

    public boolean isExecutingRequest() {
        return executingRequests > 0;
    }

    public ITicketStorageRead getTicketsStorage() {
        return ticketsStorage;
    }

    /**
     * @return true if at leas one request run
     */
    private boolean runRequests() {
        List<TicketsToLoad> ticketsToLoad = ticketsStorage.getTicketsToLoad();
        if (ticketsToLoad != null && ticketsToLoad.size() != 0) {
            for (TicketsToLoad toLoad : ticketsToLoad) {
                Transport.INSTANCE.getTickets(toLoad, this);
                ++executingRequests;
            }
            return true;
        } else {
            return false;
        }
    }

    private void failAllDemands(@NonNull Exception e) {
        for (LotteryTicketDemand demand : lotteryTicketDemands) {
            demand.listener.onNetworkRequestError(e);
        }
        lotteryTicketDemands.clear();
    }

    public void reset() {
        ticketsStorage.reset(Lottery.values());
    }

    private static class LotteryTicketDemand {
        public final TicketsType type;
        public final int minAmount;
        final GetObjectCallback<ITicketStorageRead> listener;

        public LotteryTicketDemand(TicketsType type, int minAmount, GetObjectCallback<ITicketStorageRead> listener) {
            this.type = type;
            this.minAmount = minAmount;
            this.listener = listener;
        }
    }
}

