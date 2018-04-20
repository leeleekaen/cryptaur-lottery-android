package com.cryptaur.lottery.model;

import android.support.annotation.Nullable;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Money;

public class TicketFeeUpdater implements NetworkRequest.NetworkRequestListener<Money> {

    private final Draw draw;
    @Nullable
    private final GetObjectCallback<Money> callback;

    public TicketFeeUpdater(Draw draw, @Nullable GetObjectCallback<Money> callback) {
        this.draw = draw;
        this.callback = callback;
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {

    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, Money responce) {
        draw.setTicketPrice(responce);
        if (callback != null)
            callback.onRequestResult(responce);
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        if (callback != null)
            callback.onNetworkRequestError(e);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        if (callback != null)
            callback.onCancel();
    }
}
