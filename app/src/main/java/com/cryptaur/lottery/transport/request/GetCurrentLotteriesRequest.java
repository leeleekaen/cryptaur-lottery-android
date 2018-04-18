package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class GetCurrentLotteriesRequest extends BaseLotteryRequest<CurrentDraws> {
    private static final String METHOD = "api/getCurrentLotteries";

    public GetCurrentLotteriesRequest(OkHttpClient client, NetworkRequestListener<CurrentDraws> listener) {
        super(client, listener);
    }

    @Override
    protected CurrentDraws parseJson(JSONObject source) throws JSONException {

        JSONArray draws = source.getJSONArray("draws");
        Draw[] drawsArr = new Draw[draws.length()];
        for (int i = 0; i < draws.length(); i++) {
            drawsArr[i] = new Draw(draws.getJSONObject(i));
        }
        return new CurrentDraws(drawsArr);
    }

    @Override
    protected void execRequest() {
        execSimpleRequest(METHOD, RequestType.Get, null);
    }
}
