package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.JSONObjectHelper;
import com.cryptaur.lottery.transport.model.Money;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class GetTicketPriceRequest extends BaseLotteryRequest<Money> {
    private static final String METHOD = "api/getTicketPrice/";
    private final Draw draw;
    private final String address;

    public GetTicketPriceRequest(Draw draw, String address, OkHttpClient client, NetworkRequestListener<Money> listener) {
        super(client, listener);
        this.draw = draw;
        this.address = address;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = String.format(Locale.US, "%s%d/%d/%s", METHOD, draw.lottery.getServerId(), draw.number, address);
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
