package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;

public class Money implements Serializable {
    public final BigInteger amount;
    public final BigInteger fee;
    public final long timestamp;

    public Money(BigInteger amount, BigInteger fee) {
        this.amount = amount;
        this.fee = fee;
        timestamp = System.currentTimeMillis();
    }

    public Money(JSONObject src) throws JSONException {
        timestamp = src.getLong("T");
        JSONObjectHelper helper = new JSONObjectHelper(src);
        amount = helper.getUnsignedBigInteger("A");
        fee = helper.getUnsignedBigInteger("F");
    }

    public long age() {
        return System.currentTimeMillis() - timestamp;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jso = new JSONObject();
        jso.put("T", timestamp);
        JSONObjectHelper helper = new JSONObjectHelper(jso);
        helper.put("A", amount)
                .put("F", fee);
        return helper.getJson();
    }
}
