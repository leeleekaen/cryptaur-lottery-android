package com.cryptaur.lottery.model;

import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.base.NetworkRequest;

import java.util.ArrayList;
import java.util.List;

@UiThread
public class RequestJoiner<T> implements NetworkRequest.NetworkRequestListener<T> {

    private final List<GetObjectCallback<T>> getObjectCallbacks = new ArrayList<>();
    private T value;
    private boolean executingRequest;
    private long responceTimestamp;


    public RequestJoiner() {
    }

    public void addCallback(GetObjectCallback<T> callback) {
        if (!getObjectCallbacks.contains(callback))
            getObjectCallbacks.add(callback);
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, T responce) {
        value = responce;
        responceTimestamp = System.currentTimeMillis();
        executingRequest = false;
        for (int i = 0; i < getObjectCallbacks.size(); i++) {
            getObjectCallbacks.get(i).onRequestResult(responce);
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

    public boolean isResultOutdated(long timeout) {
        return value == null || System.currentTimeMillis() - responceTimestamp > timeout;
    }

    public T getValue() {
        return value;
    }
}
