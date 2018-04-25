package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.WinTicketReply;
import com.cryptaur.lottery.transport.model.WinTicketsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.OkHttpClient;

public class GetWinTicketsRequest extends BaseLotteryRequest<WinTicketReply> {
    private final WinTicketsRequest request;

    private final String METHOD = "api/getWinTickets";

    public GetWinTicketsRequest(WinTicketsRequest request, OkHttpClient client, NetworkRequestListener<WinTicketReply> listener) {
        super(client, listener);
        this.request = request;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = String.format(Locale.US, "%s/%d/%d/%d/%d", METHOD,
                request.draw.lottery.getServerId(), request.draw.number, request.start, request.amount);
        execSimpleRequest(method, RequestType.Get, null);
    }

    @Override
    protected WinTicketReply parseJson(JSONObject source) throws JSONException {
        return new WinTicketReply(request, source);
    }
}
