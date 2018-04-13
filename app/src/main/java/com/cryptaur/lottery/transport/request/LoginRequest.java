package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class LoginRequest extends BaseLotteryRequest<Session> {

    private static final String METHOD = "api/login";
    private final Login login;

    public LoginRequest(OkHttpClient client, Login login, NetworkRequestListener listener) {
        super(client, listener);
        this.login = login;
    }

    @Override
    protected void execRequest() throws JSONException {
        JSONObject requestJson = new JSONObject();
        if (login.login != null && login.password != null){
            requestJson.put("login", login.login);
            requestJson.put("password", login.password);
            if (login.pin != null)
                requestJson.put("pin", login.pin);
            requestJson.put("key", "0x203040");
        } else {
            requestJson.put("pin", login.pin);
            requestJson.put("nonce", 1);
            requestJson.put("salt", "replace it");
            requestJson.put("signature", "TODO:");
        }
        execSimpleRequest(METHOD, RequestType.Post, requestJson.toString());
    }

    @Override
    protected Session parseJson(JSONObject source) throws JSONException {
        return new Session(source);
    }
}
