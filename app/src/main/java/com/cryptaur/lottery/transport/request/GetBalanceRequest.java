package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.JSONObjectHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

import okhttp3.OkHttpClient;

public class GetBalanceRequest extends BaseLotteryRequest<BigInteger> {

    private static final String METHOD = "api/getPlayerAviableBalance/";
    private final String address;

    public GetBalanceRequest(String address, OkHttpClient client, NetworkRequestListener<BigInteger> listener) {
        super(client, listener);
        this.address = address;
    }

    @Override
    protected void execRequest() throws JSONException {
        execSimpleRequest(METHOD + address, RequestType.Get, null);
    }

    @Override
    protected BigInteger parseJson(JSONObject source) throws JSONException {
        return new JSONObjectHelper(source).getUnsignedBigInteger("balance");
    }
}
