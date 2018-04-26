package com.cryptaur.lottery.transport.model;

import android.util.Log;

import com.cryptaur.lottery.Const;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionGetTheWin extends Transaction {

    public final Money winAmount;

    public TransactionGetTheWin(String transactionHash, Money winAmount) {
        super(transactionHash);
        this.winAmount = winAmount;
    }

    public TransactionGetTheWin(JSONObject src) throws JSONException {
        super(src);
        winAmount = new Money(src.getJSONObject("W"));
    }

    public static TransactionGetTheWin fromJson(JSONObject source) {
        if (source.isNull("W"))
            return null;
        try {
            return new TransactionGetTheWin(source);
        } catch (JSONException e) {
            Log.e(Const.TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject res = super.toJson();
        res.put("W", winAmount.toJson());
        return res;
    }
}
