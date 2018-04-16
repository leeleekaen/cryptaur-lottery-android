package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.exception.ServerException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

public abstract class BaseLotteryRequest<Result> extends NetworkRequest<Result> {

    public BaseLotteryRequest(OkHttpClient client, NetworkRequestListener<Result> listener) {
        super(client, listener);
    }

    @Override
    public void run() {
        if (listener != null)
            listener.onNetworkRequestStart(this);
        try {
            execRequest();
        } catch (JSONException e) {
            if (finish() && listener != null) {
                listener.onNetworkRequestError(this, e);
            }
        }
    }

    @Override
    protected Result parse(String source, int status) throws IOException, JSONException {
        JSONObject res;

        res = new JSONObject(source);

        if (res.has("errorCode")) {
            int code = res.getInt("errorCode");
            String message = res.isNull("errorMessage") ? null : res.getString("errorMessage");
            throw new ServerException(code, message);
        }
        return parseJson(res);
    }

    protected abstract void execRequest() throws JSONException;

    protected abstract Result parseJson(JSONObject source) throws JSONException;
}
