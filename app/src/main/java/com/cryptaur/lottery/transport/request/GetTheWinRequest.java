package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.GetTheWinTransactionObject;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.transport.model.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class GetTheWinRequest extends BaseLotteryRequest<Transaction> {

    private static final String METHOD = "api/pickUpWin";

    private final Money winAmount;

    public GetTheWinRequest(Money amount, Session session, OkHttpClient client, NetworkRequestListener<Transaction> listener) {
        super(client, listener);
        winAmount = amount;
        setAuthString(session.key);
    }

    @Override
    protected void execRequest() throws JSONException {
        execSimpleRequest(METHOD, RequestType.Post, "{}");
    }

    @Override
    protected Transaction parseJson(JSONObject source) throws JSONException {
        String hash = source.getString("trxHash");
        GetTheWinTransactionObject trx = new GetTheWinTransactionObject(winAmount, System.currentTimeMillis());
        return new Transaction(hash, trx);
    }
}
