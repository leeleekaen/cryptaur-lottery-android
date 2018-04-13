package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Session {
    public final String key;
    public final String address;

    public Session(String key, String address) {
        this.key = key;
        this.address = address;
    }

    public Session(JSONObject source) throws JSONException {
        key = source.getString("session");
        address = source.getString("playerAddress");
    }
}
