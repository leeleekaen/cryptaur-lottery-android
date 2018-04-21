package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.TicketsToLoad;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.OkHttpClient;

public class GetTicketsRequest extends BaseLotteryRequest<LotteryTicketsList> {
    private static final String METHOD = "api/getPlayerTickets";
    private final TicketsToLoad toLoad;
    private final String address;

    public GetTicketsRequest(TicketsToLoad toLoad, String address, OkHttpClient client, NetworkRequestListener<LotteryTicketsList> listener) {
        super(client, listener);
        this.toLoad = toLoad;
        this.address = address;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = String.format(Locale.US, "%s/%d/%s/%d/%d", METHOD, toLoad.lottery.getServerId(), address, toLoad.start, toLoad.amount);
        execSimpleRequest(method, RequestType.Get, null);
    }

    @Override
    protected LotteryTicketsList parseJson(JSONObject source) throws JSONException {
        return new LotteryTicketsList(toLoad.lottery, toLoad.start, source);
    }
}
