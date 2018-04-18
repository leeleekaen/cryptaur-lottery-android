package com.cryptaur.lottery.transport.request;

import android.content.Context;

import com.cryptaur.lottery.BuildConfig;
import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.transport.base.RequestLog;
import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginRequest extends BaseLotteryRequest<Session> {

    private static final String METHOD = "connect/token";
    private final Login login;
    private final String deviceId;
    private final Context context;

    public LoginRequest(Context context, OkHttpClient client, Login login, String deviceId, NetworkRequestListener<Session> listener) {
        super(client, listener);
        this.context = context.getApplicationContext();
        this.login = login;
        this.deviceId = deviceId;
    }

    @Override
    protected void execRequest() throws JSONException {
        execSimpleRequest(METHOD, RequestType.Post, null);
    }

    @Override
    protected Request createRequest(String method, RequestType requestType, String requestBody) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", login.login.toString())
                .add("password", login.password.toString())
                .add("deviceId", deviceId)
                .add("scope", "lottery_main offline_access")
                .add("pin", login.pin.toString())
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

    public Login getLogin() {
        return login;
    }

    public Context getContext() {
        return context;
    }
}
