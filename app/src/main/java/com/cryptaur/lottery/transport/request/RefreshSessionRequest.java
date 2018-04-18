package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.BuildConfig;
import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.transport.base.RequestLog;
import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RefreshSessionRequest extends BaseLotteryRequest<Session> implements ISessionRequest {
    private static final String METHOD = "connect/token";
    private volatile Session oldSession;

    public RefreshSessionRequest(OkHttpClient client, Session oldSession, NetworkRequestListener<Session> listener) {
        super(client, listener);
        this.oldSession = oldSession;
    }

    @Override
    protected void execRequest() throws JSONException {
        execSimpleRequest(METHOD, RequestType.Post, null);
    }

    @Override
    protected Request createRequest(String method, RequestType requestType, String requestBody) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", oldSession.getRefreshToken())
                .build();

        Request.Builder builder = new Request.Builder()
                .url(Const.AUTH_URL + METHOD)
                .method(requestType.requestTypeString(), formBody)
                .addHeader("Authorization", "Basic cG9ydGFibGUuY2xpZW50OnNlY3JldA==")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Content-Type", "application/x-www-form-urlencoded");

        Request request = builder.build();

        if (BuildConfig.DEBUG) {
            debugData = new RequestLog(requestType);
            debugData.onStart(request);
        }
        return request;
    }

    @Override
    protected Session parseJson(JSONObject source) throws JSONException {
        return new Session(source);
    }

    @Override
    public void setSession(Session session) {
        this.oldSession = session;
    }
}
