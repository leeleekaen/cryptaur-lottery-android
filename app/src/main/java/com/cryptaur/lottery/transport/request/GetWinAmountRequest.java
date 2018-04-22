package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.JSONObjectHelper;
import com.cryptaur.lottery.transport.model.Money;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

import okhttp3.OkHttpClient;

public class GetWinAmountRequest extends BaseLotteryRequest<Money> {

    private static final String METHOD = "api/getWinAmount/";
    private final String address;

    public GetWinAmountRequest(String address, OkHttpClient client, NetworkRequestListener<Money> listener) {
        super(client, listener);
        this.address = address;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = METHOD + address;
        execSimpleRequest(method, RequestType.Get, null);
    }

    @Override
    protected Money parseJson(JSONObject source) throws JSONException {
        JSONObjectHelper helper = new JSONObjectHelper(source);
        BigInteger amount = helper.getUnsignedBigInteger("winAmount");
        BigInteger fee = helper.getUnsignedBigInteger("pickUpWinGasFee");
        return new Money(amount, fee);
    }
}
