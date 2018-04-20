package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.BuildConfig;
import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.base.RequestLog;
import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.exception.ServerException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class BaseLotteryRequest<Result> extends NetworkRequest<Result> {

    private String authString;

    public BaseLotteryRequest(OkHttpClient client, NetworkRequestListener<Result> listener) {
        super(client, listener);
    }

    protected Request createRequest(String method, RequestType requestType, String requestBody) throws IOException {
        Request.Builder builder = new Request.Builder().url(Const.SERVER_URL + method);
        if (requestType.isEnclosing() && requestBody != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);
            builder.method(requestType.requestTypeString(), body)
                    .addHeader("Content-Type", "application/json");
        } else {
            builder.method(requestType.requestTypeString(), null);
        }
        if (authString != null) {
            builder.addHeader("Cache-Control", "no-cache")
                    .addHeader("Authorization", "Bearer " + authString);
        }
        //builder.addHeader("Accept", "application/json");

        Request request = builder.build();

        if (BuildConfig.DEBUG) {
            debugData = new RequestLog(requestType);
            debugData.onStart(request);
        }

        return request;
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
        if (source.length() > 0) {
            res = new JSONObject(source);
            if (res.has("errorCode")) {
                int code = res.getInt("errorCode");
                String message = res.isNull("errorMessage") ? null : res.getString("errorMessage");
                throw new ServerException(code, message);
            }
            return parseJson(res);
        } else
            return parseJson(null);
    }

    public void setAuthString(String authString) {
        this.authString = authString;
    }

    protected abstract void execRequest() throws JSONException;

    protected abstract Result parseJson(JSONObject source) throws JSONException;
}
