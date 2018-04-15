package com.cryptaur.lottery.model;

import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.TicketsList;

import java.util.ArrayList;
import java.util.List;

@UiThread
public class TicketsRequestJoiner implements NetworkRequest.NetworkRequestListener<TicketsList> {

    private final List<GetObjectCallback<ITicketStorageRead>> getObjectCallbacks = new ArrayList<>();
    private final TicketsStorage ticketsStorage = new TicketsStorage();
    private boolean executingRequest;
    private long responceTimestamp;


    public TicketsRequestJoiner() {

    }

    public void addCallback(GetObjectCallback<ITicketStorageRead> callback) {
        if (!getObjectCallbacks.contains(callback))
            getObjectCallbacks.add(callback);
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, TicketsList responce) {
        ticketsStorage.addTickets(responce);
        responceTimestamp = System.currentTimeMillis();
        executingRequest = false;
        for (int i = 0; i < getObjectCallbacks.size(); i++) {
            getObjectCallbacks.get(i).onRequestResult(ticketsStorage);
        }
        getObjectCallbacks.clear();
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        executingRequest = false;
        for (int i = 0; i < getObjectCallbacks.size(); i++) {
            getObjectCallbacks.get(i).onNetworkRequestError(e);
        }
        getObjectCallbacks.clear();
    }

    @Override
    public void onCancel(NetworkRequest request) {
        executingRequest = false;
        for (int i = 0; i < getObjectCallbacks.size(); i++) {
            getObjectCallbacks.get(i).onCancel();
        }
        getObjectCallbacks.clear();
    }

    public boolean isExecutingRequest() {
        return executingRequest;
    }

    public void setExecutingRequest(boolean executingRequest) {
        this.executingRequest = executingRequest;
    }

    public long getResponceTimestamp() {
        return responceTimestamp;
    }

    public TicketsStorage getTicketsStorage() {
        return ticketsStorage;
    }
}
