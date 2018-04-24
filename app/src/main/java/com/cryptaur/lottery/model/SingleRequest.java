package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.base.NetworkRequest;

public class SingleRequest<T> implements NetworkRequest.NetworkRequestListener<T> {

    private final RequestExecutor<T> requestExecutor;
    private boolean executingRequest = false;
    private RequestDoneListener<T> requestDoneListener;
    private RequestAbortListener requestAbortListener;

    public SingleRequest(RequestExecutor<T> requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    public void executeRequest() {
        if (!executingRequest) {
            requestExecutor.executeRequest(this);
        }
    }

    public void setRequestDoneListener(RequestDoneListener<T> requestDoneListener) {
        this.requestDoneListener = requestDoneListener;
    }

    public void setRequestAbortListener(RequestAbortListener requestAbortListener) {
        this.requestAbortListener = requestAbortListener;
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, T responce) {
        executingRequest = false;
        if (requestDoneListener != null)
            requestDoneListener.onRequestDone(responce);
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        executingRequest = false;
        if (requestAbortListener != null)
            requestAbortListener.onRequestAbort(e);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        executingRequest = false;
        if (requestAbortListener != null)
            requestAbortListener.onRequestAbort(null);
    }

    public interface RequestExecutor<T> {
        void executeRequest(NetworkRequest.NetworkRequestListener<T> listener);
    }

    public interface RequestDoneListener<T> {
        void onRequestDone(T result);
    }

    public interface RequestAbortListener {
        void onRequestAbort(Exception e);
    }
}
