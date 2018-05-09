package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.transport.model.TransactionState;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class CheckTransactionStateRequest extends BaseLotteryRequest<TransactionState> {

    private static final String METHOD = "api/getTrxStatus/";
    private final Transaction transaction;

    public CheckTransactionStateRequest(Transaction transaction, OkHttpClient client, NetworkRequestListener<TransactionState> listener) {
        super(client, listener);
        this.transaction = transaction;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = METHOD + transaction.transactionHash;
        execSimpleRequest(method, RequestType.Get, null);
    }

    @Override
    protected TransactionState parseJson(JSONObject source) throws JSONException {
        String statusStr = source.getString("trxStatus");
        return new TransactionState(transaction, TransactionState.State.valueOf(statusStr));
    }
}
