package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class RefreshSessionRequest extends BaseLotteryRequest<Session> implements ISessionRequest{
    private static final String METHOD = "api/refresh/";
    private volatile Session oldSession;

    public RefreshSessionRequest(OkHttpClient client, Session oldSession, NetworkRequestListener<Session> listener) {
        super(client, listener);
        this.oldSession = oldSession;
    }

    @Override
    protected void execRequest() throws JSONException {
        String request = METHOD + oldSession.key;
        execSimpleRequest(request, RequestType.Get, null);
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
