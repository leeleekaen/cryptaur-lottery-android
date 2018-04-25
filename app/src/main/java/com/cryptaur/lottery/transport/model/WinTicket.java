package com.cryptaur.lottery.transport.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

public class WinTicket {
    public final String playerAddress;
    public final int index;
    public final int winLevel;
    public final BigInteger winAmount;
    public final Draw draw;

    public WinTicket(Draw draw, JSONObject source) throws JSONException {
        this.draw = draw;
        JSONObjectHelper helper = new JSONObjectHelper(source);
        playerAddress = source.isNull("playerAddres") ? source.getString("playerAddress") :
                source.getString("playerAddres");
        index = source.getInt("index");
        winLevel = source.getInt("winLevel");
        winAmount = helper.getUnsignedBigInteger("winAmount");
    }
}