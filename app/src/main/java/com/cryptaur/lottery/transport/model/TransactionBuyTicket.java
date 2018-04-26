package com.cryptaur.lottery.transport.model;

import android.util.Log;

import com.cryptaur.lottery.Const;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionBuyTicket extends Transaction {

    public final Ticket ticket;

    public TransactionBuyTicket(String transactionHash, Ticket ticket) {
        super(transactionHash);
        this.ticket = ticket;
    }

    public TransactionBuyTicket(JSONObject src) throws JSONException {
        super(src);
        ticket = new Ticket(src.getJSONObject("TT"));
    }

    public static TransactionBuyTicket fromJson(JSONObject source) {
        if (source.isNull("TT"))
            return null;
        try {
            return new TransactionBuyTicket(source);
        } catch (JSONException e) {
            Log.e(Const.TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject res = super.toJson();
        res.put("TT", ticket.toJson());
        return res;
    }
}
