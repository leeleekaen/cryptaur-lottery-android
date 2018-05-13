package com.cryptaur.lottery.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.TicketsToLoad;
import com.cryptaur.lottery.transport.model.TicketsType;
import com.cryptaur.lottery.transport.request.GetTicketsRequest;

import java.util.ArrayList;
import java.util.List;

@UiThread
class TicketsKeeper implements NetworkRequest.NetworkRequestListener<LotteryTicketsList> {

    private final TicketsStorage2 ticketsStorage = new TicketsStorage2(Lottery.values());
    private final List<LotteryTicketsDemand> lotteryTicketDemands = new ArrayList<>();
    private final List<TicketsToLoad> updateRequests = new ArrayList<>();
    private final List<SimpleGetObjectCallback<ITicketStorageRead>> listeners = new ArrayList<>();
    private int executingRequests = 0;

    public TicketsKeeper(Keeper keeper) {
        keeper.currentDrawsKeeper.addListener(ticketsStorage::onCurrentDrawsUpdated);
    }

    public void onCurrentDrawsUpdated(CurrentDraws draws) {
        ticketsStorage.onCurrentDrawsUpdated(draws);
    }

    public void requestTicketStorage(TicketsType type, int minAmount, @Nullable SimpleGetObjectCallback<ITicketStorageRead> listener) {
        if (executingRequests > 0) {
            lotteryTicketDemands.add(new LotteryTicketsDemandByType(type, minAmount, listener));
            return;
        }

        runTicketsUpdate();

        if (ticketsStorage.checkCanReturnRequest(type, minAmount)) {
            if (listener != null)
                listener.onRequestResult(ticketsStorage);
        } else {
            LotteryTicketsDemand demand = new LotteryTicketsDemandByType(type, minAmount, listener);
            lotteryTicketDemands.add(demand);
            if (!runRequests()) {
                if (listener != null)
                    listener.onRequestResult(ticketsStorage);
                lotteryTicketDemands.remove(demand);
            }
        }
    }

    void runTicketsUpdate() {
        if (executingRequests == 0) {
            List<TicketsToLoad> updateRequests = ticketsStorage.getTicketsToUpdate();
            if (updateRequests != null && updateRequests.size() > 0) {
                this.updateRequests.addAll(updateRequests);
                executingRequests += updateRequests.size();
                for (TicketsToLoad updateRequest : updateRequests) {
                    Transport.INSTANCE.getTickets(updateRequest, this);
                }
            }
        }
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, LotteryTicketsList responce) {
        --executingRequests;
        boolean isUpdateRequest = removeUpdateRequest(request);
        ticketsStorage.add(responce, isUpdateRequest);

        if (updateRequests.size() > 0)
            return;

        for (int i = 0; i < lotteryTicketDemands.size(); i++) {
            LotteryTicketsDemand demand = lotteryTicketDemands.get(i);
            if (ticketsStorage.checkCanReturnRequest(demand)) {
                if (demand.listener != null)
                    demand.listener.onRequestResult(ticketsStorage);
                lotteryTicketDemands.remove(i--);
            }
        }

        if (executingRequests == 0 && lotteryTicketDemands.size() > 0) {
            if (!runRequests()) {
                failAllDemands(new Exception("no requests run with existing demands"));
            }
        }
        if (executingRequests == 0)
            for (SimpleGetObjectCallback<ITicketStorageRead> listener : listeners) {
                listener.onRequestResult(ticketsStorage);
            }
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        --executingRequests;
        removeUpdateRequest(request);
        if (executingRequests == 0)
            failAllDemands(e);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        --executingRequests;
        removeUpdateRequest(request);
        if (executingRequests == 0) {
            for (LotteryTicketsDemand demand : lotteryTicketDemands) {
                if (demand.listener instanceof GetObjectCallback)
                    ((GetObjectCallback) demand.listener).onCancel();
            }
        }
    }

    private boolean removeUpdateRequest(NetworkRequest request) {
        if (request instanceof GetTicketsRequest && updateRequests.size() > 0) {
            return updateRequests.remove(((GetTicketsRequest) request).toLoad);
        }
        return false;
    }

    public ITicketStorageRead getTicketsStorage() {
        return ticketsStorage;
    }

    /**
     * @return true if at leas one request run
     */
    boolean runRequests() {
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

    public boolean isExecutingRequests() {
        return executingRequests > 0;
    }

    private void failAllDemands(@NonNull Exception e) {
        for (LotteryTicketsDemand demand : lotteryTicketDemands) {
            if (demand.listener instanceof GetObjectCallback)
                ((GetObjectCallback) demand.listener).onNetworkRequestError(e);
        }
        lotteryTicketDemands.clear();
    }

    public void reset() {
        ticketsStorage.reset(Lottery.values());
    }

    public void addListener(SimpleGetObjectCallback<ITicketStorageRead> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        listener.onRequestResult(ticketsStorage);
    }

    public void removeListener(GetObjectCallback<ITicketStorageRead> listener) {
        listeners.remove(listener);
    }

    public void requestTicketStorage(Lottery lottery, int drawNumber, SimpleGetObjectCallback<ITicketStorageRead> simpleGetObjectCallback) {

    }
}

