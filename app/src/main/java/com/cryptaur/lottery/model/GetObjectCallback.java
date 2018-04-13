package com.cryptaur.lottery.model;

public interface GetObjectCallback<T> {
    void onRequestResult(T responce);

    void onNetworkRequestError(Exception e);

    void onCancel();
}
