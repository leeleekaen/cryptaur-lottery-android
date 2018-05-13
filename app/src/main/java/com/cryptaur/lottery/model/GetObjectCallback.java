package com.cryptaur.lottery.model;

public interface GetObjectCallback<T> extends SimpleGetObjectCallback<T> {
    void onNetworkRequestError(Exception e);

    void onCancel();
}
