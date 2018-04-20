package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.JSONObjectHelper;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.Money;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class GetTicketPriceRequest extends BaseLotteryRequest<Money> {
    private static final String METHOD = "api/getTicketPrice/";
    final Lottery lottery;

    public GetTicketPriceRequest(Lottery lottery, OkHttpClient client, NetworkRequestListener<Money> listener) {
        super(client, listener);
        this.lottery = lottery;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = String.format(Locale.US, "%s%d", METHOD, lottery.getServerId());
        execSimpleRequest(method, RequestType.Get, null);
    }

    @Override
    protected Money parseJson(JSONObject source) throws JSONException {
        JSONObjectHelper helper = new JSONObjectHelper(source);
        BigInteger price = helper.getUnsignedBigInteger("price");
        BigInteger fee = helper.getUnsignedBigInteger("gasFee");
        return new Money(price, fee);
    }
}
