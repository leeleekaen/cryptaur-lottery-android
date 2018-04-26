package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Transaction {
    public final String transactionHash;
    public final long timestamp;

    public Transaction(String transactionHash) {
        this.transactionHash = transactionHash;
        timestamp = System.currentTimeMillis();
    }

    public Transaction(JSONObject src) throws JSONException {
        transactionHash = src.getString("H");
        timestamp = src.getLong("T");
    }

    public static Transaction parse(JSONObject src) {
        Transaction res = TransactionBuyTicket.fromJson(src);
        if (res != null)
            return res;

        return TransactionGetTheWin.fromJson(src);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject res = new JSONObject();
        res.put("H", transactionHash);
        res.put("T", timestamp);
        return res;
    }
}
