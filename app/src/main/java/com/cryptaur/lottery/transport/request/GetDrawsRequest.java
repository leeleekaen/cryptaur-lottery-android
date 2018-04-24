package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.DrawsReply;
import com.cryptaur.lottery.transport.model.DrawsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.OkHttpClient;

public class GetDrawsRequest extends BaseLotteryRequest<DrawsReply> {

    private static final String METHOD = "api/getDraws";
    private final DrawsRequest request;

    public GetDrawsRequest(DrawsRequest request, OkHttpClient client, NetworkRequestListener<DrawsReply> listener) {
        super(client, listener);
        this.request = request;
    }

    @Override
    protected void execRequest() throws JSONException {
        String method = String.format(Locale.US, "%s/%d/%d/%d", METHOD, request.lottery.getServerId(), request.start, request.count);
        execSimpleRequest(method, RequestType.Get, null);
    }

    @Override
    protected DrawsReply parseJson(JSONObject source) throws JSONException {
        return new DrawsReply(request, source);
    }
}
