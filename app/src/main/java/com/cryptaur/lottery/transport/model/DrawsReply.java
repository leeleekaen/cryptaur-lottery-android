package com.cryptaur.lottery.transport.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawsReply {
    public final List<Draw> draws;
    public final Lottery lottery;
    public final int start;

    public DrawsReply(DrawsRequest request, JSONObject source) throws JSONException {
        this.start = request.start;
        this.lottery = request.lottery;
        JSONArray draws = source.getJSONArray("draws");
        List<Draw> result = new ArrayList<>();

        for (int i = 0; i < draws.length(); ++i) {
            result.add(new Draw(lottery, draws.getJSONObject(i)));
        }
        this.draws = Collections.unmodifiableList(result);
    }
}
