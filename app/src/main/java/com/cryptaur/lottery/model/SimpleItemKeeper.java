package com.cryptaur.lottery.model;

import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.base.NetworkRequest;

import java.util.ArrayList;
import java.util.List;

@UiThread
class SimpleItemKeeper<T> implements NetworkRequest.NetworkRequestListener<T> {

    private final List<GetObjectCallback<T>> dynamicCallbacks = new ArrayList<>();
    private final List<GetObjectCallback<T>> updateListeners = new ArrayList<>();
    private final long valueTimeout;
    private final Executor<T> executor;
    private T value;
    private boolean executingRequest;
    private long responceTimestamp;

    SimpleItemKeeper(long valueTimeout, Executor<T> executor) {
        this.valueTimeout = valueTimeout;
        this.executor = executor;
    }

    private void addDynamicCallback(GetObjectCallback<T> callback) {
        if (!dynamicCallbacks.contains(callback))
            dynamicCallbacks.add(callback);
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, T responce) {
        value = responce;
        responceTimestamp = System.currentTimeMillis();
        executingRequest = false;
        for (int i = 0; i < dynamicCallbacks.size(); i++) {
            dynamicCallbacks.get(i).onRequestResult(responce);
        }
        dynamicCallbacks.clear();
        for (int i = 0; i < updateListeners.size(); i++) {
            updateListeners.get(i).onRequestResult(responce);
        }
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        executingRequest = false;
        for (int i = 0; i < dynamicCallbacks.size(); i++) {
            dynamicCallbacks.get(i).onNetworkRequestError(e);
        }
        dynamicCallbacks.clear();
    }

    @Override
    public void onCancel(NetworkRequest request) {
        executingRequest = false;
        for (int i = 0; i < dynamicCallbacks.size(); i++) {
            dynamicCallbacks.get(i).onCancel();
        }
        dynamicCallbacks.clear();
    }

    public long getResponceTimestamp() {
        return responceTimestamp;
    }

    private boolean isResultOutdated(long timeout) {
        return value == null || System.currentTimeMillis() - responceTimestamp > timeout;
    }

    public T getValue() {
        return value;
    }

    @UiThread
    public void requestValue(final GetObjectCallback<T> listener, boolean forceUpdate) {
        if (executingRequest) {
            addDynamicCallback(listener);
        } else {
            if (forceUpdate || isResultOutdated(valueTimeout)) {
                executingRequest = true;
                addDynamicCallback(listener);
                executor.executeRequest(this);
            }
        }

        if (value != null && !forceUpdate) {
            listener.onRequestResult(value);
        }
    }

    public void addListener(GetObjectCallback<T> listener) {
        if (!updateListeners.contains(listener))
            updateListeners.add(listener);
        if (value != null)
            listener.onRequestResult(value);
    }

    public void removeListener(GetObjectCallback<T> listener) {
        updateListeners.remove(listener);
    }

    public interface Executor<T> {
        void executeRequest(NetworkRequest.NetworkRequestListener<T> listener);
    }
}
