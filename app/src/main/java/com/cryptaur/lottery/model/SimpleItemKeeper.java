package com.cryptaur.lottery.model;

import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.base.NetworkRequest;

import java.util.ArrayList;
import java.util.List;

@UiThread
class SimpleItemKeeper<T> implements NetworkRequest.NetworkRequestListener<T> {

    private final List<SimpleGetObjectCallback<T>> dynamicCallbacks = new ArrayList<>();
    private final List<SimpleGetObjectCallback<T>> updateListeners = new ArrayList<>();
    private final long valueTimeout;
    private final Executor<T> executor;
    protected T value;
    private boolean executingRequest;
    private long responceTimestamp;

    SimpleItemKeeper(long valueTimeout, Executor<T> executor) {
        this.valueTimeout = valueTimeout;
        this.executor = executor;
    }

    private void addDynamicCallback(SimpleGetObjectCallback<T> callback) {
        if (callback != null && !dynamicCallbacks.contains(callback))
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
            SimpleGetObjectCallback<T> callback = dynamicCallbacks.get(i);
            if (callback instanceof GetObjectCallback) {
                ((GetObjectCallback) callback).onNetworkRequestError(e);
            }
        }
        dynamicCallbacks.clear();
    }

    @Override
    public void onCancel(NetworkRequest request) {
        executingRequest = false;
        for (int i = 0; i < dynamicCallbacks.size(); i++) {
            SimpleGetObjectCallback<T> callback = dynamicCallbacks.get(i);
            if (callback instanceof GetObjectCallback) {
                ((GetObjectCallback) callback).onCancel();
            }
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
    public void requestValue(final SimpleGetObjectCallback<T> listener, boolean forceUpdate) {
        if (executingRequest) {
            addDynamicCallback(listener);
        } else {
            if (forceUpdate || isResultOutdated(valueTimeout)) {
                executingRequest = true;
                addDynamicCallback(listener);
                executor.executeRequest(this);
            }
        }

        if (listener != null && value != null && !forceUpdate) {
            listener.onRequestResult(value);
        }
    }

    public void addListener(SimpleGetObjectCallback<T> listener) {
        if (!updateListeners.contains(listener))
            updateListeners.add(listener);
        if (value != null)
            listener.onRequestResult(value);
    }

    public void removeListener(SimpleGetObjectCallback<T> listener) {
        updateListeners.remove(listener);
    }

    public void clear() {
        value = null;
    }

    public interface Executor<T> {
        void executeRequest(NetworkRequest.NetworkRequestListener<T> listener);
    }
}
