package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Session {
    private static final long EXPIRE_GAP = 15_000;//

    public final String key;
    public final String address;
    private final long expiresIn;
    private final String refreshToken;
    private final String tokenType;
    private final long timeStamp;
    private final long expireTimestamp;


    public Session(String key, String address) {
        this.key = key;
        this.address = address;
        expiresIn = 900_000;
        refreshToken = null;
        tokenType = "";
        timeStamp = System.currentTimeMillis();
        expireTimestamp = timeStamp + expiresIn - EXPIRE_GAP;
    }

    public Session(JSONObject source) throws JSONException {
        timeStamp = System.currentTimeMillis();
        key = source.getString("access_token");
        address = source.getString("address");
        expiresIn = source.getInt("expires_in") * 1000;
        refreshToken = source.getString("refresh_token");
        tokenType = source.getString("token_type");
        expireTimestamp = timeStamp + expiresIn - EXPIRE_GAP;
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() - timeStamp) < expiresIn;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
